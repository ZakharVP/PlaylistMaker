package com.practicum.playlistmaker.playlist.search.ui.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.playlist.search.ui.viewmodels.SearchState
import com.practicum.playlistmaker.playlist.search.ui.viewmodels.SearchViewModel
import com.practicum.playlistmaker.playlist.sharing.data.models.Track
import com.practicum.playlistmaker.ui.compose.TrackItem
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FindScreen(
    viewModel: SearchViewModel,
    onTrackClick: (Track) -> Unit,
    onBackClick: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val currentQuery by viewModel.currentQuery.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.main_screen_find),
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
    ){ paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // Search Field
            SearchField(
                query = currentQuery,
                onQueryChange = { query ->
                    viewModel.setCurrentQuery(query)
                    if (query.isNotEmpty()) {
                        viewModel.searchDebounced(query)
                    } else {
                        viewModel.showHistory()
                    }
                },
                onClearClick = {
                    viewModel.setCurrentQuery("")
                    viewModel.showHistory()
                },
                onSearchClick = {
                    focusManager.clearFocus()
                    if (currentQuery.isNotEmpty()) {
                        viewModel.searchDebounced(currentQuery)
                    }
                },
                modifier = Modifier.padding(16.dp)
            )

            when (state) {
                is SearchState.Loading -> {
                    LoadingState()
                }
                is SearchState.Content -> {
                    val tracks = (state as SearchState.Content).tracks
                    if (tracks.isEmpty()) {
                        EmptySearchState()
                    } else {
                        TrackList(
                            tracks = tracks,
                            onTrackClick = onTrackClick
                        )
                    }
                }
                is SearchState.History -> {
                    val historyState = state as SearchState.History
                    if (historyState.tracks.isNotEmpty()) {
                        SearchHistoryState(
                            tracks = historyState.tracks,
                            onTrackClick = onTrackClick,
                            onClearHistory = { viewModel.clearHistory() }
                        )
                    } else {
                        EmptyHistoryState()
                    }
                }
                is SearchState.Empty -> {
                    EmptySearchState()
                }
                is SearchState.NetworkError -> {
                    NetworkErrorState(
                        onRetryClick = { viewModel.retryLastSearch() }
                    )
                }
                is SearchState.EmptyHistory -> {
                    EmptyHistoryState()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    onClearClick: () -> Unit,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var text by remember { mutableStateOf(query) }

    LaunchedEffect(query) {
        text = query
    }

    OutlinedTextField(
        value = text,
        onValueChange = {
            text = it
            onQueryChange(it)
        },
        modifier = modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = stringResource(R.string.search_hint),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        leadingIcon = {
            Icon(
                painter = painterResource(R.drawable.ic_search),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingIcon = {
            if (text.isNotEmpty()) {
                IconButton(onClick = {
                    text = ""
                    onClearClick()
                }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_clear),
                        contentDescription = stringResource(R.string.clear)
                    )
                }
            }
        },
        singleLine = true,
        shape = MaterialTheme.shapes.small,
        colors = TextFieldDefaults.colors(),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = { onSearchClick() }
        )
    )
}

@Composable
private fun TrackList(
    tracks: List<Track>,
    onTrackClick: (Track) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(tracks) { track ->
            TrackItem(
                track = track,
                onClick = { onTrackClick(track) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun SearchHistoryState(
    tracks: List<Track>,
    onTrackClick: (Track) -> Unit,
    onClearHistory: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = stringResource(R.string.history),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            textAlign = TextAlign.Center
        )

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(tracks) { track ->
                TrackItem(
                    track = track,
                    onClick = { onTrackClick(track) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Button(
            onClick = onClearHistory,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = stringResource(R.string.clear_history))
        }
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun EmptySearchState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.no_songs),
                contentDescription = null,
                modifier = Modifier.size(120.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.no_find_songs),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun EmptyHistoryState() {
    EmptySearchState()
}

@Composable
private fun NetworkErrorState(
    onRetryClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(R.drawable.no_network_light_mode),
                contentDescription = null,
                modifier = Modifier.size(120.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.network_error_top_message),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.network_error_bottom_message),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRetryClick) {
                Text(text = stringResource(R.string.network_error_button))
            }
        }
    }
}