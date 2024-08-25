package com.practicum.playlistmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val button_search = findViewById<Button>(R.id.b_search)
        /*val button_searchClickListener: View.OnClickListener = object : View.OnClickListener{
            override fun onClick(v: View?) {
                Toast.makeText(this@MainActivity, R.string.b_action_search, Toast.LENGTH_SHORT).show()
                }
        }
        button_search.setOnClickListener(button_searchClickListener)*/
        button_search.setOnClickListener{
            val displayIntent = Intent(this, FindActivity::class.java)
            startActivity(displayIntent)
        }

        val button_media = findViewById<Button>(R.id.b_media)
        button_media.setOnClickListener {
            //Toast.makeText(this@MainActivity,R.string.b_action_media, Toast.LENGTH_SHORT).show()
            val displayIntent = Intent(this, MediatekaActivity::class.java)
            startActivity(displayIntent)
        }

        val button_settings = findViewById<Button>(R.id.b_settings)
        button_settings.setOnClickListener {
            //Toast.makeText(this@MainActivity,R.string.b_action_settings, Toast.LENGTH_SHORT).show()
            val displayIntent = Intent(this, SettingsActivity::class.java)
            startActivity(displayIntent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}