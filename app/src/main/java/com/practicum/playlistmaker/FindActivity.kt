package com.practicum.playlistmaker

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
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FindActivity : AppCompatActivity() {

    private lateinit var editText: EditText
    private lateinit var noSongsView: LinearLayout
    private lateinit var networkView: LinearLayout
    private lateinit var imageScreenNetworkError: ImageView
    private lateinit var imageScreenNoFindSongs: ImageView
    private lateinit var recyclerView: RecyclerView

    private var saveText: String = ""
    private var textSearch: String = ""

    val itunesBaseUrl = "https://itunes.apple.com/"
    val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val itunesService = retrofit.create(ItunesApplicationApi::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_find)

        val button_back = findViewById<Button>(R.id.backToMain)
        button_back.setOnClickListener{
            val displayIntent = Intent(this, MainActivity::class.java)
            startActivity(displayIntent)
        }
        editText = findViewById(R.id.findEditText)
        val clearButton = findViewById<ImageView>(R.id.clearIcon)

        saveText = savedInstanceState?.getString("text", "") ?: ""
        editText.setText(saveText)

        clearButton.setOnClickListener{
            editText.setText("")
        }

        noSongsView = findViewById<LinearLayout>(R.id.no_songs)
        networkView = findViewById<LinearLayout>(R.id.network_error)
        imageScreenNoFindSongs = findViewById<ImageView>(R.id.no_songs_screen)
        imageScreenNetworkError = findViewById<ImageView>(R.id.network_error_screen)
        recyclerView = findViewById(R.id.recyclerView)

        networkView.visibility = View.GONE
        noSongsView.visibility = View.GONE

        val buttonUpdate = findViewById<Button>(R.id.button_update)
        buttonUpdate.setOnClickListener{
            findSongs(editText.text.toString())
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = clearButtonVisibility(s)
                textSearch = s.toString()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {
                saveText = s.toString()
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("text_key", saveText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        saveText = savedInstanceState.getString("text_key","")
        editText.setText(saveText)
    }

    private fun isNightMode(): Boolean {
        return (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
    }

    private fun findSongs(textSearch: String){
        val call = itunesService.search(textSearch)
        call.enqueue(object : Callback<SongResponse> {
            override fun onResponse(call: Call<SongResponse>, response: Response<SongResponse>) {
                Log.d("FindActivity", "Ответ получен после отправки: $textSearch")
                Log.d("FindActivity", "Код ответа сервера: ${response.code()}")
                if (response.isSuccessful) {
                    Log.d("FindActivity", "Результат: ${response.body()?.results.toString()}")
                    Log.d("FindActivity", "Найдено треков: ${response.body()?.resultCount}")
                    val songsList = response.body()?.results ?: emptyList()

                    if (songsList.isNotEmpty()) {
                        recyclerView.visibility = View.VISIBLE
                        networkView.visibility = View.GONE
                        noSongsView.visibility = View.GONE
                        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
                        val trackAdapter = TrackAdapter(songsList)
                        recyclerView.adapter = trackAdapter
                    } else{
                        recyclerView.visibility = View.GONE
                        networkView.visibility = View.GONE
                        noSongsView.visibility = View.VISIBLE
                        if(isNightMode()) {
                            imageScreenNoFindSongs.setImageResource(R.drawable.no_songs_dark_mode)
                        }else {
                            imageScreenNoFindSongs.setImageResource(R.drawable.no_songs_light_mode)
                        }
                    }

                } else {
                    Log.e("FindActivity", "Ошибка: ${response.code()}")
                    recyclerView.visibility = View.GONE
                    noSongsView.visibility = View.GONE
                    networkView.visibility = View.VISIBLE
                    if(isNightMode()) {
                        imageScreenNetworkError.setImageResource(R.drawable.no_network_dark_mode)
                    }else {
                        imageScreenNetworkError.setImageResource(R.drawable.no_network_light_mode)
                    }
                }
            }
            override fun onFailure(call: Call<SongResponse>, t: Throwable) {
                Log.d("FindActivity", "Ошибка: ${t.message}")
            }
        })

    }
}