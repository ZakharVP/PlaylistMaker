package com.practicum.playlistmaker.presentation.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.data.sharedPreferences.ThemeManager

class SettingsActivity : AppCompatActivity() {

    private lateinit var themeSwitch: Switch

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        themeSwitch = findViewById<Switch>(R.id.switchTheme)
        val isNightMode = ThemeManager.getThemeFromPreferences(this)
        val tool_bar_button_back = findViewById<Toolbar>(R.id.toolBarSettings)
        val colorIcon = if (isNightMode) {
            themeSwitch.setChecked(true)
            R.color.yp_white
        } else {
            themeSwitch.setChecked(false)
            R.color.yp_black
        }
        tool_bar_button_back.navigationIcon?.setTint(ContextCompat.getColor(this,colorIcon))
        tool_bar_button_back.setNavigationOnClickListener {
            val displayIntent = Intent(this, MainActivity::class.java)
            startActivity(displayIntent)
        }

        themeSwitch.setOnClickListener() {
            if (themeSwitch.isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            ThemeManager.saveThemeToPreferences(this, themeSwitch.isChecked)
        }

        val button_share = findViewById<Button>(R.id.share)
        button_share.setOnClickListener{
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, getString(R.string.url_share))
                type = "text/plain"
            }
            startActivity(Intent.createChooser(shareIntent,getString(R.string.title_share)))
        }

        val button_send_to_support = findViewById<Button>(R.id.sendToSupport)
        button_send_to_support.setOnClickListener{
            val email = getString(R.string.mail_address)
            val subject = getString(R.string.mail_theme)
            val body = getString(R.string.mail_body)

            val sendLetter = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:$email")
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, body)
            }
            startActivity(sendLetter)
        }
        val button_agreement = findViewById<Button>(R.id.agreement)
        button_agreement.setOnClickListener{
            val url = getString(R.string.offer_address)
            val openUrlAgreement = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(url)
            }
            startActivity(openUrlAgreement)
        }
    }
}