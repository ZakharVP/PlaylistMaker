package com.practicum.playlistmaker.presentation.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.ConstantsApp.Config
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.data.sharedPreferences.ThemeManager
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.presentation.adapters.TrackAdapter
import com.practicum.playlistmaker.data.sharedPreferences.TrackManager
import com.practicum.playlistmaker.domain.OnTrackClickListener
import com.practicum.playlistmaker.domain.impl.HistoryRepositoryImpl
import com.practicum.playlistmaker.domain.impl.TrackRepositoryImpl
import com.practicum.playlistmaker.domain.interactors.HistoryUseCase
import com.practicum.playlistmaker.domain.interactors.SearchTracksUseCase

class FindActivity : AppCompatActivity(), OnTrackClickListener {

    private lateinit var editText: EditText
    private lateinit var noSongsView: LinearLayout
    private lateinit var networkView: LinearLayout
    private lateinit var imageScreenNetworkError: ImageView
    private lateinit var imageScreenNoFindSongs: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchHint: TextView
    private lateinit var buttonClearHistory: Button
    private lateinit var clearButton: ImageButton
    private lateinit var scrollView: ScrollView
    private lateinit var progressBar: ProgressBar

    private lateinit var searchUseCase: SearchTracksUseCase
    private lateinit var historyUseCase: HistoryUseCase

    private val networkClient = RetrofitNetworkClient()

    private var saveText: String = ""
    private var textSearch: String = ""

    var isNightMode : Boolean = false
    val songsList: ArrayList<Track> = ArrayList()
    val songsListReverse: ArrayList<Track> = ArrayList()
    var trackAdapter: TrackAdapter = TrackAdapter(this, songsList, this)

    private val searchRunnable = Runnable { findSongs(textSearch) }
    private val handler = Handler(Looper.getMainLooper())
    private var isClickAllowed = true
    private var historyIsHide : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        searchUseCase = Creator.provideSearchTracksUseCase()
        historyUseCase = Creator.provideHistoryUseCase(this)



        setContentView(R.layout.activity_find)

        // *** Блок инициализации View. Начало *** //
        val tool_bar_button_back = findViewById<Toolbar>(R.id.toolBarFind)
        editText = findViewById(R.id.findEditText)
        clearButton = findViewById<ImageButton>(R.id.clearIcon)
        scrollView = findViewById<ScrollView>(R.id.scrollViewOne)
        progressBar = findViewById<ProgressBar>(R.id.progressBar)
        searchHint = findViewById(R.id.searchHint)
        recyclerView = findViewById(R.id.recyclerView)
        buttonClearHistory = findViewById(R.id.clearHistory)
        noSongsView = findViewById<LinearLayout>(R.id.no_songs)
        imageScreenNoFindSongs = findViewById<ImageView>(R.id.no_songs_screen)
        networkView = findViewById<LinearLayout>(R.id.network_error)
        imageScreenNetworkError = findViewById<ImageView>(R.id.network_error_screen)
        // *** Блок инициализации View. Окончание *** //

        val isNightMode = ThemeManager.getThemeFromPreferences(this)
        val colorIcon = if (isNightMode) { R.color.yp_white } else { R.color.yp_black}
        tool_bar_button_back.navigationIcon?.setTint(ContextCompat.getColor(this,colorIcon))
        tool_bar_button_back.setNavigationOnClickListener {
            val displayIntent = Intent(this, MainActivity::class.java)
            startActivity(displayIntent)
        }

        val colorClearButtonIcon = if (isNightMode) { R.color.yp_black } else { R.color.yp_gray }
        clearButton.setColorFilter(ContextCompat.getColor(this, colorClearButtonIcon))
        clearButton.visibility = View.GONE
        saveText = savedInstanceState?.getString("text", "") ?: ""
        editText.setText(saveText)

        // **** Первый показ экрана.
        hideHistory()
        hideNoSongs()
        hideNoNetwork()

        // **** Установили активной строку.
        // Если есть история - отображаем, если нет то обычный пустой экран.
        editText.setOnFocusChangeListener{_, hasFocus ->
            if (hasFocus) {
                val songsListString = TrackManager.getHistoryTrack(this)
                if (songsListString.size == 0){
                    hideHistory()
                } else{
                  // Нужно отобразить историю просмотра
                    showHistory()
                }
            }
        }

        buttonClearHistory.setOnClickListener{
            TrackManager.clearTrackFromPreferences(this)
            hideHistory()
        }

        clearButton.setOnClickListener{
            editText.setText("")
            if (networkView.isVisible){ networkView.visibility = View.GONE }
            if (noSongsView.isVisible) { noSongsView.visibility = View.GONE }
            // Строка остается в фокусе. Если есть история - нужно показать
            showHistory()
        }

        val buttonUpdate = findViewById<Button>(R.id.button_update)
        buttonUpdate.setOnClickListener{
            findSongs(editText.text.toString())
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = clearButtonVisibility(s)
                textSearch = s.toString()
                if (!historyIsHide) { hideHistory() }
                searchDebounce(s.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    if (networkView.isVisible) { networkView.visibility = View.GONE }
                    if (noSongsView.isVisible) { noSongsView.visibility = View.GONE }
                    // Строка остается в фокусе. Если есть история - нужно показать
                    showHistory()
                }
            }
        }
        editText.addTextChangedListener(simpleTextWatcher)

        editText.setOnEditorActionListener{ _, actionId, _ ->
            if(noSongsView.isVisible) { noSongsView.visibility = View.GONE }
            if(networkView.isVisible) { networkView.visibility = View.GONE }
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                Log.d("FindActivity","Текст поиска: $textSearch")
                findSongs(textSearch)
                true
            } else {
                false
            }
        }
    }

    override fun onTrackClick(track: Track) {

        if (clickDebounce()) {
            val intent = Intent(this, AudioPlayer::class.java)

            intent.putExtra("trackId",track.trackId)
            intent.putExtra("trackId",track.trackId)
            intent.putExtra("trackName",track.trackName)
            intent.putExtra("trackTimeMillis",track.trackTimeMillisString)
            intent.putExtra("artistName",track.artistName)
            intent.putExtra("artworkUrl100",track.artworkUrl)
            intent.putExtra("collectionName",track.collectionName)
            intent.putExtra("primaryGenreName",track.genre)
            intent.putExtra("year",track.releaseYear.take(4))
            intent.putExtra("country",track.country)
            intent.putExtra("previewUrl",track.previewUrl)

            startActivity(intent)
        }
    }

    private fun searchDebounce(someChar : String) {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, Config.SEARCH_DEBOUNCE_DELAY)
    }

    private fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true}, Config.CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun clearHistoryVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun hideHistory() {
        searchHint.visibility = View.GONE
        buttonClearHistory.visibility = View.GONE
        scrollView.visibility = View.GONE
        historyIsHide = true
    }

    private fun hideNoNetwork(){
        networkView.visibility = View.GONE
    }

    private fun hideNoSongs() {
        noSongsView.visibility = View.GONE
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("text_key", saveText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        saveText = savedInstanceState.getString("text_key","")
        editText.setText(saveText)
    }

    private fun findSongs(textSearch: String) {
        scrollView.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
        Log.d("findSongs","Begin getting ...")
        Thread {
            try {
                val tracks = searchUseCase.execute(textSearch)
                Log.d("findSongs","count of songs - ${tracks.count()}")

                runOnUiThread {
                    progressBar.visibility = View.GONE

                    if (tracks.isNotEmpty()) {
                        showTracks(tracks)
                    } else {
                        showEmptyState()
                    }
                }
            } catch (e: Exception) {
                Log.d("findSongs","error ${e}")
                runOnUiThread {
                    progressBar.visibility = View.GONE
                    showError()
                }
            }
        }.start()

    }

    private fun showTracks(tracks: List<Track>) {
        hideHistory()
        scrollView.visibility = View.VISIBLE
        networkView.visibility = View.GONE
        noSongsView.visibility = View.GONE

        trackAdapter = TrackAdapter(this, tracks, this)
        recyclerView.adapter = trackAdapter
    }

    private fun showHistory() {
        val historyTracks = historyUseCase.getHistory()
        if (historyTracks.isNotEmpty()) {
            trackAdapter = TrackAdapter(this, historyTracks, this)
            recyclerView.adapter = trackAdapter
            // Показать UI истории
        } else {
            hideHistory()
        }
    }

    private fun showError() {
        // Скрываем другие состояния
        hideHistory()
        scrollView.visibility = View.GONE
        noSongsView.visibility = View.GONE

        // Показываем ошибку сети
        networkView.visibility = View.VISIBLE

        // Устанавливаем соответствующую иконку в зависимости от темы
        if (isNightMode) {
            imageScreenNetworkError.setImageResource(R.drawable.no_network_dark_mode)
        } else {
            imageScreenNetworkError.setImageResource(R.drawable.no_network_light_mode)
        }

        // Настраиваем кнопку "Обновить"
        val buttonUpdate = findViewById<Button>(R.id.button_update)
        buttonUpdate.setOnClickListener {
            // Повторяем поиск при нажатии
            findSongs(editText.text.toString())
        }
    }

    private fun showEmptyState() {
        // Скрываем другие состояния
        hideHistory()
        scrollView.visibility = View.GONE
        networkView.visibility = View.GONE

        // Показываем состояние "ничего не найдено"
        noSongsView.visibility = View.VISIBLE

        // Устанавливаем соответствующую иконку в зависимости от темы
        if (isNightMode) {
            imageScreenNoFindSongs.setImageResource(R.drawable.no_songs_dark_mode)
        } else {
            imageScreenNoFindSongs.setImageResource(R.drawable.no_songs_light_mode)
        }
    }

    private fun handleNetworkError(){
        Log.d("FindActivity", "Попали в событие handleNetworkError")
        hideHistory()
        if(noSongsView.isVisible) { noSongsView.visibility = View.GONE }
        networkView.visibility = View.VISIBLE

        if (isNightMode) {
            imageScreenNetworkError.setImageResource(R.drawable.no_network_dark_mode)
        } else {
            imageScreenNetworkError.setImageResource(R.drawable.no_network_light_mode)
        }
    }
    private fun handleError(t: Throwable){
        Log.d("FindActivity", "Ошибка : ${t.message}")

    }
    private fun handleError(code: Int) {
        Log.d("FindActivity", "Сетевая ошибка с кодом: $code")
    }

    fun pxToDp(sizePx: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, sizePx.toFloat() / resources.displayMetrics.density, resources.displayMetrics).toInt()
    }

}