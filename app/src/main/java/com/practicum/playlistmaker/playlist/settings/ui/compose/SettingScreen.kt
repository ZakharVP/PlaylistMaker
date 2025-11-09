package com.practicum.playlistmaker.playlist.settings.ui.compose

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.playlist.settings.ui.SettingsViewModel
import com.practicum.playlistmaker.ui.PlaylistMakerTheme
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    isDarkTheme: Boolean = false,
    onThemeChange: (Boolean) -> Unit = {},
    onShareClick: () -> Unit = {},
    onSupportClick: () -> Unit = {},
    onTermsClick: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.main_screen_settings),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 20.sp
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_back),
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Theme switch - отдельный элемент с закруглениями
            SettingsSwitchItem(
                title = stringResource(R.string.settings_screen_theme),
                isChecked = isDarkTheme,
                onCheckedChange = onThemeChange,
                modifier = Modifier.padding(top = 20.dp)
            )

            // Кнопки с иконками
            SettingsButtonItem(
                title = stringResource(R.string.settings_screen_share),
                icon = R.drawable.ic_s_share,
                onClick = onShareClick
            )

            SettingsButtonItem(
                title = stringResource(R.string.settings_screen_support),
                icon = R.drawable.ic_s_support,
                onClick = onSupportClick
            )

            SettingsButtonItem(
                title = stringResource(R.string.settings_screen_agreement),
                icon = R.drawable.ic_s_arrow_forward,
                onClick = onTermsClick
            )
        }
    }
}

@Composable
private fun SettingsSwitchItem(
    title: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(62.dp)
            .padding(horizontal = 16.dp)
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        // Кастомный Switch с анимацией
        val offset by animateDpAsState(
            targetValue = if (isChecked) 20.dp else 4.dp,
            animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
            label = "switch_animation"
        )

        Box(
            modifier = Modifier
                .width(52.dp)
                .height(32.dp)
                .clickable { onCheckedChange(!isChecked) }
                .background(
                    color = if (isChecked) MaterialTheme.colorScheme.onSecondary
                    else MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.CenterStart
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .offset(x = offset)
                    .background(
                        color = MaterialTheme.colorScheme.secondary,
                        shape = CircleShape
                    )
            )
        }
    }
}

@Composable
private fun SettingsButtonItem(
    title: String,
    icon: Int,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(62.dp)
            .padding(horizontal = 16.dp)
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun SettingsScreenWithViewModel(
    viewModel: SettingsViewModel,
    onBackClick: () -> Unit = {},
    onShareClick: () -> Unit = {},
    onSupportClick: () -> Unit = {},
    onTermsClick: () -> Unit = {},
    onThemeToggle: (Boolean) -> Unit = {}
) {
    val themeState by viewModel.themeState.collectAsStateWithLifecycle()

    SettingsScreen(
        isDarkTheme = themeState.darkThemeEnabled,
        onThemeChange = { isChecked ->
            viewModel.toggleTheme()
            onThemeToggle(isChecked)
        },
        onBackClick = onBackClick,
        onShareClick = onShareClick,
        onSupportClick = onSupportClick,
        onTermsClick = onTermsClick
    )
}

@Preview
@Composable
fun SettingsScreenPreview() {
    PlaylistMakerTheme {
        SettingsScreen(
            isDarkTheme = false,
            onBackClick = {},
            onShareClick = {},
            onSupportClick = {},
            onTermsClick = {}
        )
    }
}