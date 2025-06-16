package com.practicum.playlistmaker.playlist.mediateka.ui.views

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.playlist.mediateka.ui.viewmodels.MediatekaViewModel

class MediatekaActivity : AppCompatActivity() {

    private val viewModel: MediatekaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mediateka)
        setupEdgeToEdge()
        observeViewModel()
    }

    private fun setupEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun observeViewModel() {
    }
}