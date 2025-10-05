package com.practicum.playlistmaker.playlist.mediateka.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.CardTrackBinding
import com.practicum.playlistmaker.playlist.sharing.data.models.Track
import com.practicum.playlistmaker.util.dpToPx

class PlaylistTrackViewHolder(
    private val binding: CardTrackBinding,
    private val onTrackClick: (Track) -> Unit,
    private val onTrackLongClick: (Track) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(track: Track) {
        with(binding) {
            nameTrack.text = track.trackName
            nameTrackSingers.text = track.artistName
            trackDuration.text = track.trackTimeMillisString

            // Загрузка изображения с плейсхолдером
            if (track.artworkUrl.isNotEmpty()) {
                Glide.with(itemView)
                    .load(track.artworkUrl)
                    .transform(RoundedCorners(dpToPx(4f, itemView.context)))
                    .placeholder(R.drawable.poster) // Плейсхолдер если изображение грузится
                    .error(R.drawable.poster) // Плейсхолдер если ошибка загрузки
                    .into(artWorkUrl)
            } else {
                artWorkUrl.setImageResource(R.drawable.poster) // Плейсхолдер если URL пустой
            }

            // Обработчики кликов
            itemView.setOnClickListener { onTrackClick(track) }
            buttonPlayer.setOnClickListener { onTrackClick(track) }

            // Долгое нажатие для удаления
            itemView.setOnLongClickListener {
                onTrackLongClick(track)
                true
            }

            buttonPlayer.setOnLongClickListener {
                onTrackLongClick(track)
                true
            }
        }
    }
}