package com.practicum.playlistmaker.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.playlist.sharing.data.models.Track

@Composable
fun TrackItem(
    track: Track,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .height(62.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Обложка трека
            AsyncImage(
                model = track.artworkUrl,
                contentDescription = stringResource(R.string.track_cover),
                modifier = Modifier
                    .size(44.dp)
                    .clip(MaterialTheme.shapes.small),
                contentScale = ContentScale.FillBounds
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Основной контент
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(end = 8.dp)
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = track.trackName,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = track.artistName,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 4.dp)
                    )

                    // Точка-разделитель
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .clip(MaterialTheme.shapes.extraSmall)
                            .background(MaterialTheme.colorScheme.onSurfaceVariant)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = track.trackTimeMillisString,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Стрелка (кнопка)
            IconButton(
                onClick = onClick,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.light_mode_arrow_forward),
                    contentDescription = stringResource(R.string.button_forward),
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
}