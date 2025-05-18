package com.practicum.playlistmaker.presentation.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.ConstantsApp.Config
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.data.sharedPreferences.ThemeManager
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.presentation.adapters.TrackAdapter
import com.practicum.playlistmaker.domain.OnTrackClickListener
import com.practicum.playlistmaker.hide
import com.practicum.playlistmaker.show
import java.io.IOException
import kotlin.concurrent.thread

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
    private lateinit var trackAdapter: TrackAdapter

    private var currentTracks: List<Track> = emptyList()

    private val searchUseCase = Creator.provideSearchTracksUseCase()
    private val historyUseCase = Creator.provideHistoryUseCase(this)

    private var saveText: String = ""
    private var textSearch: String = ""
    private var isNightMode : Boolean = false

    private val searchRunnable = Runnable { findSongs(textSearch) }
    private val handler = Handler(Looper.getMainLooper())
    private var isClickAllowed = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find)

        initViews()
        setupTheme()
        restoreState(savedInstanceState)
        setupSearchField()
        setupToolbar()
        setupRecyclerView()
        setupButtons()

        //checkAndShowHistory()
    }

    private fun initViews() {
        findViewById<Toolbar>(R.id.toolBarFind).apply {
            setNavigationOnClickListener {
                startActivity(Intent(this@FindActivity, MainActivity::class.java))
            }
        }

        editText                    = findViewById(R.id.findEditText)
        clearButton                 = findViewById(R.id.clearIcon)
        scrollView                  = findViewById(R.id.scrollViewOne)
        progressBar                 = findViewById(R.id.progressBar)
        searchHint                  = findViewById(R.id.searchHint)
        recyclerView                = findViewById(R.id.recyclerView)
        buttonClearHistory          = findViewById(R.id.clearHistory)
        noSongsView                 = findViewById(R.id.no_songs)
        imageScreenNoFindSongs      = findViewById(R.id.no_songs_screen)
        networkView                 = findViewById(R.id.network_error)
        imageScreenNetworkError     = findViewById(R.id.network_error_screen)

    }

    private fun setupTheme() {
        isNightMode = ThemeManager.getThemeFromPreferences(this)
        val colorIcon = if (isNightMode) { R.color.yp_white } else { R.color.yp_black }
        findViewById<Toolbar>(R.id.toolBarFind).navigationIcon?.setTint(
            ContextCompat.getColor(this, colorIcon)
        )

        searchHint.hide()
        buttonClearHistory.hide()
    }

    private fun checkAndShowHistory() {
        networkView.hide()
        noSongsView.hide()

        if (editText.text.isNullOrEmpty()) {
            showHistoryIfAvailable()
        } else {
            // Если есть текст в поле поиска, скрываем историю
            hideHistory()
        }
    }

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolBarFind)
        setSupportActionBar(toolbar)

        // Устанавливаем иконку "назад"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val colorIcon = if (isNightMode) R.color.yp_white else R.color.yp_black
        toolbar.navigationIcon?.setTint(ContextCompat.getColor(this, colorIcon))

        // Обработка нажатия на кнопку "назад"
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun restoreState(savedInstanceState: Bundle?) {
        saveText = savedInstanceState?.getString("text_key","") ?: ""
        editText.setText(saveText)
    }

    private fun setupSearchField() {
        editText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                showHistoryIfAvailable()
            }
        }

        editText.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
                textSearch = s.toString()
                searchDebounce()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    hideErrorViews()
                    showHistoryIfAvailable()
                }
            }
        })

        editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                findSongs(textSearch)
                true
            } else {
                false
            }
        }
    }

    private fun setupRecyclerView() {
        trackAdapter = TrackAdapter(emptyList(),this)
        recyclerView.adapter = trackAdapter
    }

    private fun setupButtons() {
        clearButton.setOnClickListener {
            editText.setText("")
            hideErrorViews()
            showHistoryIfAvailable()
        }

        buttonClearHistory.setOnClickListener {
            historyUseCase.clearHistory()
            hideHistory()
        }

        findViewById<Button>(R.id.button_update).setOnClickListener {
            findSongs(editText.text.toString())
        }
    }

    override fun onTrackClick(track: Track) {
        if (clickDebounce()) {
            historyUseCase.addToHistory(track)
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

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        if (textSearch.isNotEmpty()) {
            handler.postDelayed(searchRunnable, Config.SEARCH_DEBOUNCE_DELAY)
        }
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, Config.CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun findSongs(query: String) {
        scrollView.hide()
        hideErrorViews()
        searchHint.hide()
        progressBar.show()
        Log.d("findSongs","Begin getting ...")
        thread {
            try {
                val tracks = searchUseCase.execute(query)
                Log.d("findSongs","count of songs - ${tracks.count()}")

                runOnUiThread {
                    progressBar.hide()

                    if (tracks.isNotEmpty()) {
                        showTracks(tracks)
                    } else {
                        showEmptyState()
                    }
                }
            } catch (e: Exception) {
                Log.d("findSongs","error ${e}")
                runOnUiThread {
                    progressBar.hide()
                    showError()
                }
            }
        }

    }

    private fun showTracks(tracks: List<Track>) {
        hideHistory()
        hideErrorViews()

        if (tracks.isEmpty()) {
            showEmptyState()
        } else {
            trackAdapter.updateTracks(tracks)
            scrollView.show()
        }
    }

    private fun showHistoryIfAvailable() {
        val historyTracks = historyUseCase.getHistory()
        if (historyTracks.isNotEmpty()) {
            trackAdapter.updateTracks(historyTracks)
            searchHint.show()
            buttonClearHistory.show()
            scrollView.show()
        } else {
            hideHistory()
        }
    }

    private fun hideHistory() { searchHint.hide()
        buttonClearHistory.hide()
        scrollView.hide()
    }

    private fun hideErrorViews() {

        networkView.hide()
        noSongsView.hide()
    }

    private fun showNetworkError() {
        hideHistory()
        scrollView.hide()
        noSongsView.hide()
        networkView.show()
        imageScreenNetworkError.setImageResource(
            if (isNightMode) R.drawable.no_network_dark_mode
            else R.drawable.no_network_light_mode
        )
    }

    private fun showError() {
        // Скрываем другие состояния
        hideHistory()
        scrollView.hide()
        noSongsView.hide()

        // Показываем ошибку сети
        networkView.show()

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
        hideHistory()
        scrollView.hide()
        networkView.hide()
        noSongsView.show()
        imageScreenNoFindSongs.setImageResource(
            if (isNightMode) R.drawable.no_songs_dark_mode
            else R.drawable.no_songs_light_mode
        )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("text_key", editText.text.toString())
    }

}


