package com.practicum.playlistmaker.Activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.ThemeManager
import com.practicum.playlistmaker.Track


class AudioPlayer: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audioplayer)

        val isNightMode = ThemeManager.getThemeFromPreferences(this)

        // *** Блок инициализации View. Начало *** //
        val buttonAddSingle = findViewById<ImageButton>(R.id.buttonAddSingle)
        val playButton = findViewById<ImageButton>(R.id.playButton)
        val buttonLikeSingle = findViewById<ImageButton>(R.id.buttonLikeSingle)

        val tool_bar_button_back = findViewById<Toolbar>(R.id.toolBarAudioPlayer)
        val imageSingleView = findViewById<ImageView>(R.id.imageSingle)

        val nameSingle = findViewById<TextView>(R.id.nameSingle)
        val authorSingle = findViewById<TextView>(R.id.authorSingle)
        val durationDataView = findViewById<TextView>(R.id.durationData)
        val albomDataView = findViewById<TextView>(R.id.albomData)
        val yearDataView = findViewById<TextView>(R.id.yearData)
        val genreDataView = findViewById<TextView>(R.id.genreData)
        val countryDataView = findViewById<TextView>(R.id.countryData)
        // *** Блок инициализации View. Окончание *** //

        val track = Track(
            trackName = intent.getStringExtra("trackName") ?: "",
            artistName = intent.getStringExtra("artistName") ?: "",
            trackTimeMillis = intent.getLongExtra("trackTimeMillis",0) ?: 0,
            trackTimeMillisString = getDuration(intent.getLongExtra("trackTimeMillis",0)) ?: "",
            trackId = intent.getStringExtra("trackId") ?: "",
            artworkUrl100 = intent.getStringExtra("artworkUrl100") ?: "",
            collectionName = intent.getStringExtra("collectionName") ?: "",
            releaseDate = "2000",
            primaryGenreName = intent.getStringExtra("primaryGenreName") ?: "",
            country = intent.getStringExtra("country") ?: ""
        )

        // *** Блок установки внешних данных. Начало *** //
        if (track.trackName.isNotEmpty()) {
        nameSingle.text = track.trackName
        } else {
            nameSingle.visibility = View.GONE
        }
        if (track.artistName.isNotEmpty()) {
            authorSingle.text = track.artistName
        } else {
            authorSingle.visibility = View.GONE
        }
        if (track.trackTimeMillisString.isNotEmpty()) {
            durationDataView.text = track.trackTimeMillisString
        } else {
            durationDataView.visibility = View.GONE
        }
        if (track.collectionName.isNotEmpty()) {
            albomDataView.text = track.collectionName
        } else {
            albomDataView.visibility = View.GONE
        }
        yearDataView.text = "2000"
        if (track.country.isNotEmpty()){
            countryDataView.text = track.country
        } else {
            countryDataView.visibility = View.GONE
        }


        // *** Блок установки внешних данных. Окончание *** //

        // *** Блок установки картинок для тем (светлой и темной). Начало *** //
        if (isNightMode) {
            buttonAddSingle.setImageResource(R.drawable.add_single_dark)
            playButton.setImageResource(R.drawable.play_button_dark)
            buttonLikeSingle.setImageResource(R.drawable.like_button_dark)
        } else {
            buttonAddSingle.setImageResource(R.drawable.add_single_light)
            playButton.setImageResource(R.drawable.play_button_light)
            buttonLikeSingle.setImageResource(R.drawable.like_button_light)
        }
        // *** Блок установки картинок для тем (светлой и темной). Окончание *** //


        val artBigArtUrl = track.artworkUrl100?.let { getBigArtUrl(track.artworkUrl100) }

        tool_bar_button_back.setNavigationOnClickListener {
            val displayIntent = Intent(this, FindActivity::class.java)
            startActivity(displayIntent)
        }

        Glide.with(this)
            .load(artBigArtUrl)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(100)))
                .error(R.drawable.no_image_placeholder)
            .placeholder(R.drawable.no_image_placeholder)
            .into(imageSingleView)
    }

    fun getBigArtUrl(originalUrl: String): String{
        return originalUrl.replace("100x100bb.jpg", "512x512bb.jpg")
    }

    fun getDuration(duration: Long): String {
        val totalSeconds = duration / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}