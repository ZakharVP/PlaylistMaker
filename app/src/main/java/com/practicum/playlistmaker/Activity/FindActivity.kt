package com.practicum.playlistmaker.Activity

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
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
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Visibility
import com.google.gson.Gson
import com.practicum.playlistmaker.ConstantsApp.PLAYLIST_SETTINGS
import com.practicum.playlistmaker.ConstantsApp.PLAYLIST_SETTINGS_THEME_NIGHT_VALUE
import com.practicum.playlistmaker.ItunesApplicationApi
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.RetrofitFactory
import com.practicum.playlistmaker.SongResponse
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find)

        val sharedPreferences = getSharedPreferences(PLAYLIST_SETTINGS, MODE_PRIVATE)
        isNightMode = sharedPreferences.getBoolean(PLAYLIST_SETTINGS_THEME_NIGHT_VALUE, false)
        if (isNightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        // *** Блок инициализации Вьюх. Начало *** //
        val tool_bar_button_back = findViewById<Toolbar>(R.id.toolBarFind)
        searchHint = findViewById(R.id.searchHint)
        buttonClearHistory = findViewById(R.id.clearHistory)
        editText = findViewById(R.id.findEditText)
        clearButton = findViewById<ImageButton>(R.id.clearIcon)
        noSongsView = findViewById<LinearLayout>(R.id.no_songs)
        networkView = findViewById<LinearLayout>(R.id.network_error)
        imageScreenNoFindSongs = findViewById<ImageView>(R.id.no_songs_screen)
        imageScreenNetworkError = findViewById<ImageView>(R.id.network_error_screen)
        recyclerView = findViewById(R.id.recyclerView)
        scrollView = findViewById<ScrollView>(R.id.scrollViewOne)
        // *** Блок инициализации Вьюх. Окончание *** //

        val colorIcon = if (isNightMode) { R.color.yp_white } else { R.color.yp_black }
        tool_bar_button_back.navigationIcon?.setTint(ContextCompat.getColor(this,colorIcon))
        tool_bar_button_back.setNavigationOnClickListener {
            val displayIntent = Intent(this, MainActivity::class.java)
            startActivity(displayIntent)
        }

        val colorClearButtonIcon = if (isNightMode) { R.color.yp_black } else { R.color.yp_gray }
        clearButton.setColorFilter(ContextCompat.getColor(this, colorClearButtonIcon))
        saveText = savedInstanceState?.getString("text", "") ?: ""
        editText.setText(saveText)

        buttonClearHistory.setOnClickListener{
            TrackManager.clearTrackFromPreferences(this)
            songsList.clear()
            trackAdapter = TrackAdapter(this, songsList)
            recyclerView.adapter = trackAdapter
            searchHint.visibility = View.GONE
            buttonClearHistory.visibility = View.GONE
        }

        clearButton.setOnClickListener{
            editText.setText("")
            if (recyclerView.visibility == View.VISIBLE) {
                trackAdapter = TrackAdapter(this, songsListReverse)
                recyclerView.adapter = trackAdapter
            }
            if (networkView.visibility == View.VISIBLE){
                networkView.visibility = View.GONE
            }
            if (noSongsView.visibility == View.VISIBLE){
                noSongsView.visibility = View.GONE
            }
            if (songsList.isNotEmpty()) {
                searchHint.visibility = View.VISIBLE
                recyclerView.visibility = View.VISIBLE
                buttonClearHistory.visibility = View.VISIBLE
            }
        }

        editText.setOnFocusChangeListener{_, hasFocus ->
            if (hasFocus) {
                val songsListString = TrackManager.getHistoryTrack(this)
                songsList.clear()
                songsListReverse.clear()
                for (trackString in songsListString) {
                    if (trackString.isNotEmpty()) {
                        val track = gson.fromJson(trackString, Track::class.java)
                        songsList.add(track)
                    }
                }
                if (songsList.isNotEmpty()) {
                    buttonClearHistory.visibility = View.VISIBLE
                    searchHint.visibility = View.VISIBLE
                    for( track in songsList.reversed()){
                       songsListReverse.add(track)
                    }
                    trackAdapter = TrackAdapter(this, songsListReverse)
                    recyclerView.adapter = trackAdapter
                    scrollView.visibility = View.VISIBLE
                }
            }
        }

        val buttonUpdate = findViewById<Button>(R.id.button_update)
        buttonUpdate.setOnClickListener{
            findSongs(editText.text.toString())
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                // Если строка ввода пустая, то нужно показать историю
                if (s.isNullOrEmpty()){
                    songsList.clear()
                    songsListReverse.clear()
                    val songsListString = TrackManager.getHistoryTrack(this@FindActivity)
                    for (trackString in songsListString){
                        if(trackString.isNotEmpty()){
                            val track = gson.fromJson(trackString, Track::class.java)
                            songsList.add(track)
                        }
                    }
                    for( track in songsList.reversed()){
                        songsListReverse.add(track)
                    }
                    trackAdapter = TrackAdapter(this@FindActivity, songsListReverse)
                    recyclerView.adapter = trackAdapter
                }else {
                    clearButton.visibility = clearButtonVisibility(s)
                    searchHint.visibility = clearHistoryVisibility(s)
                    recyclerView.visibility = clearHistoryVisibility(s)
                    buttonClearHistory.visibility = clearHistoryVisibility(s)
                    textSearch = s.toString()
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {
                saveText = s.toString()
                if (saveText.isEmpty()) {
                    if (recyclerView.visibility == View.VISIBLE) {
                        trackAdapter = TrackAdapter(this@FindActivity, songsList)
                        recyclerView.adapter = trackAdapter
                    }
                    if (networkView.visibility == View.VISIBLE){
                        networkView.visibility = View.GONE
                    }
                    if (noSongsView.visibility == View.VISIBLE){
                        noSongsView.visibility = View.GONE
                    }
        }
    }
        }
        editText.addTextChangedListener(simpleTextWatcher)

        editText.setOnEditorActionListener{ _, actionId, _ ->
            noSongsView.visibility = View.GONE
            networkView.visibility = View.GONE
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
                        if (networkView.visibility == View.VISIBLE) {
                            networkView.visibility = View.GONE
                        }
                        if (noSongsView.visibility == View.VISIBLE) {
                            noSongsView.visibility = View.GONE
                        }
                        if (recyclerView.visibility == View.GONE) {
                            recyclerView.visibility = View.VISIBLE
                        }
                        val trackAdapter = TrackAdapter(this@FindActivity,songsList)
                        recyclerView.adapter = trackAdapter
                    } else {
                        if (recyclerView.visibility == View.VISIBLE) {
                            trackAdapter = TrackAdapter(this@FindActivity, songsList)
                            recyclerView.adapter = trackAdapter
                            recyclerView.visibility = View.GONE
                        }
                        if (networkView.visibility == View.VISIBLE) {
                            networkView.visibility = View.GONE
                        }
                        if (noSongsView.visibility == View.GONE){
                            noSongsView.visibility = View.VISIBLE
                        }

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
        recyclerView.visibility = View.GONE
        noSongsView.visibility = View.GONE
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

}