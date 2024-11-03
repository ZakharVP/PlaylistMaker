package com.practicum.playlistmaker.Activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.practicum.playlistmaker.ConstantsApp.PLAYLIST_SETTINGS
import com.practicum.playlistmaker.ConstantsApp.PLAYLIST_SETTINGS_THEME_NIGHT_VALUE
import com.practicum.playlistmaker.ItunesApplicationApi
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.RetrofitFactory
import com.practicum.playlistmaker.SongResponse
import com.practicum.playlistmaker.ThemeManager
import com.practicum.playlistmaker.Track
import com.practicum.playlistmaker.TrackAdapter
import com.practicum.playlistmaker.TrackManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class FindActivity : AppCompatActivity() {

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

    private var saveText: String = ""
    private var textSearch: String = ""

    val retrofit = RetrofitFactory.create()
    val itunesService = retrofit.create(ItunesApplicationApi::class.java)
    var isNightMode : Boolean = false
    val songsList: ArrayList<Track> = ArrayList()
    val songsListReverse: ArrayList<Track> = ArrayList()
    var trackAdapter: TrackAdapter = TrackAdapter(this, songsList)
    val gson = Gson()
    private var historyIsHide : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find)

        // *** Блок инициализации View. Начало *** //
        val tool_bar_button_back = findViewById<Toolbar>(R.id.toolBarFind)
        editText = findViewById(R.id.findEditText)
        clearButton = findViewById<ImageButton>(R.id.clearIcon)
        scrollView = findViewById<ScrollView>(R.id.scrollViewOne)
        searchHint = findViewById(R.id.searchHint)
        recyclerView = findViewById(R.id.recyclerView)
        buttonClearHistory = findViewById(R.id.clearHistory)
        noSongsView = findViewById<LinearLayout>(R.id.no_songs)
        imageScreenNoFindSongs = findViewById<ImageView>(R.id.no_songs_screen)
        networkView = findViewById<LinearLayout>(R.id.network_error)
        imageScreenNetworkError = findViewById<ImageView>(R.id.network_error_screen)
        // *** Блок инициализации View. Окончание *** //

        val colorIcon = if (isNightMode) { R.color.yp_white } else { R.color.yp_black }
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

        //val height36px = pxToDp(36)
        //buttonClearHistory.layoutParams.height = height36px

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

    private fun showHistory() {
        songsList.clear()
        songsListReverse.clear()
        val songsListString = TrackManager.getHistoryTrack(this@FindActivity)
        if (songsListString.size != 0) {
            for (trackString in songsListString) {
                if (trackString.isNotEmpty()) {
                    val track = gson.fromJson(trackString, Track::class.java)
                    songsList.add(track)
                }
            }
            for (track in songsList.reversed()) {
                songsListReverse.add(track)
            }
            trackAdapter = TrackAdapter(this, songsListReverse)
            recyclerView.adapter = trackAdapter

            searchHint.visibility = View.VISIBLE
            buttonClearHistory.visibility = View.VISIBLE
            scrollView.visibility = View.VISIBLE

            //scrollView.layoutParams.height = resources.getDimension(R.dimen.primary_indent_size_320).toInt()
            //scrollView.requestLayout()
            historyIsHide = false
        } else {
            hideHistory()
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
        Log.d("FindActivity", "Перед вызовом сервиса iTunes")
        val call = itunesService.search(textSearch)
        Log.d("FindActivity", "После вызова сервиса iTunes")

        call.enqueue(object : Callback<SongResponse> {
            override fun onResponse(call: Call<SongResponse>, response: Response<SongResponse>) {
                Log.d("FindActivity", "Ответ получен после отправки: $textSearch")
                Log.d("FindActivity", "Код ответа сервера: ${response.code()}")
                if (response.isSuccessful) {
                    Log.d("FindActivity", "Результат: ${response.body()?.results.toString()}")
                    Log.d("FindActivity", "Найдено треков: ${response.body()?.resultCount}")
                    val songsList = response.body()?.results ?: emptyList()

                    if (songsList.isNotEmpty()) {
                        hideHistory()
                        if (!scrollView.isVisible)  { scrollView.visibility = View.VISIBLE }
                        if (networkView.isVisible) { networkView.visibility = View.GONE }
                        if (noSongsView.isVisible) { noSongsView.visibility = View.GONE }

                        val trackAdapter = TrackAdapter(this@FindActivity,songsList)
                        recyclerView.adapter = trackAdapter
                    } else {
                        hideHistory()
                        if (scrollView.isVisible)  { scrollView.visibility = View.GONE }
                        if (networkView.isVisible) { networkView.visibility = View.GONE }
                        if (!noSongsView.isVisible) { noSongsView.visibility = View.VISIBLE }
                        if (isNightMode) {
                            imageScreenNoFindSongs.setImageResource(R.drawable.no_songs_dark_mode)
                        } else {
                            imageScreenNoFindSongs.setImageResource(R.drawable.no_songs_light_mode)
                        }
                    }
                } else {
                    handleError(response.code())
                }
            }

            override fun onFailure(call: Call<SongResponse>, t: Throwable) {
                Log.d("FindActivity", "Попали в событие onFailure")
                if (t is IOException) {
                    Log.d("FindActivity", t.toString())
                    handleNetworkError()
                } else {
                    Log.d("FindActivity", t.toString())
                    handleError(t)
                }

            }
        })
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