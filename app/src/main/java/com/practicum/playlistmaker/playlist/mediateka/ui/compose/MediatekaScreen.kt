package com.practicum.playlistmaker.playlist.mediateka.ui.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.playlist.mediateka.ui.compose.favorites.FavoritesScreen
import com.practicum.playlistmaker.playlist.mediateka.ui.compose.playlists.PlaylistsScreen

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
fun MediatekaScreen(
    onBackClick: () -> Unit,
    onCreatePlaylist: () -> Unit = {},
    onPlaylistClick: (com.practicum.playlistmaker.playlist.sharing.data.models.Playlist) -> Unit = {},
    onTrackClick: (com.practicum.playlistmaker.playlist.sharing.data.models.Track) -> Unit = {}
) {
    val tabs = listOf(
        stringResource(R.string.favorites_title),
        stringResource(R.string.playlists_title)
    )

    var selectedTabIndex by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.main_screen_media),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 20.sp
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start
                    )
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        text = { Text(title) },
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index }
                    )
                }
            }

            when (selectedTabIndex) {
                0 -> FavoritesScreen(onTrackClick = onTrackClick)
                1 -> PlaylistsScreen(
                    onCreatePlaylist = onCreatePlaylist,
                    onPlaylistClick = onPlaylistClick
                )
            }
        }
    }
}