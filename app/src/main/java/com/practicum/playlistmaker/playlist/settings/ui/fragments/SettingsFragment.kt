package com.practicum.playlistmaker.playlist.settings.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.practicum.playlistmaker.PlayMarketApplication
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.playlist.settings.ui.SettingsViewModel
import com.practicum.playlistmaker.playlist.settings.ui.compose.SettingsScreenWithViewModel
import com.practicum.playlistmaker.ui.ObserveAppTheme
import com.practicum.playlistmaker.ui.PlaylistMakerTheme
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue


class SettingsFragment : Fragment() {

    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                ObserveAppTheme { isDarkTheme ->
                    PlaylistMakerTheme(darkTheme = isDarkTheme) {
                        SettingsScreenWithViewModel(
                            viewModel = viewModel,
                            onBackClick = { onBackClick() },
                            onShareClick = { shareApp() },
                            onSupportClick = { contactSupport() },
                            onTermsClick = { openTerms() },
                            onThemeToggle = { newTheme ->
                                (requireActivity().application as PlayMarketApplication)
                                    .applyTheme(newTheme)
                            }
                        )
                    }
                }
            }
        }
    }

    private fun onBackClick() {
        parentFragmentManager.popBackStack()
    }

    private fun shareApp() {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, getString(R.string.title_share))
            type = "text/plain"
        }
        startActivity(Intent.createChooser(shareIntent, getString(R.string.title_share)))
    }

    private fun contactSupport() {
        val email = getString(R.string.mail_address)
        val subject = getString(R.string.mail_theme)
        val body = getString(R.string.mail_body)

        Intent(Intent.ACTION_SEND).apply {
            data = Uri.parse("mailto:$email")
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }.let { intent ->
            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(intent)
            } else {
                Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
                    putExtra(Intent.EXTRA_SUBJECT, subject)
                    putExtra(Intent.EXTRA_TEXT, body)
                }.also { fallbackIntent ->
                    startActivity(Intent.createChooser(fallbackIntent, getString(R.string.send_email)))
                }
            }
        }
    }

    private fun openTerms() {
        val url = getString(R.string.offer_address)
        Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
        }.also { startActivity(it) }
    }
}