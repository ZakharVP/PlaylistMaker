package com.practicum.playlistmaker

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var isDarkMode = getThemePreference()
        if (isDarkMode) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }

        setContentView(R.layout.activity_main)

        val button_search = findViewById<Button>(R.id.bSearch)
        button_search.setOnClickListener{
            val displayIntent = Intent(this, FindActivity::class.java)
            startActivity(displayIntent)
        }

        val button_media = findViewById<Button>(R.id.bMedia)
        button_media.setOnClickListener {
            val displayIntent = Intent(this, MediatekaActivity::class.java)
            startActivity(displayIntent)
        }

        val button_settings = findViewById<Button>(R.id.bSettings)
        button_settings.setOnClickListener {
            val displayIntent = Intent(this, SettingsActivity::class.java)
            startActivity(displayIntent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

    }

    private fun getThemePreference(): Boolean {
        val sharedPreferences: SharedPreferences = getSharedPreferences("appPreferences", MODE_PRIVATE)
        return sharedPreferences.getBoolean("isDarkMode", false)
    }
}