package com.practicum.playlistmaker.playlist.search.ui.adapters

import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.CardTrackBinding
import com.practicum.playlistmaker.playlist.sharing.data.models.Track
import com.practicum.playlistmaker.util.dpToPx

class TrackViewHolder(
    private val binding: CardTrackBinding,
    private val onTrackClick: (Track) -> Unit
) : androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {

    fun bind(track: Track) {
        with(binding) {
            // Используем правильные ID из card_track.xml
            nameTrack.text = track.trackName          // Было: trackName
            nameTrackSingers.text = track.artistName  // Было: artistName

            // Загрузка изображения
            Glide.with(itemView)
                .load(track.artworkUrl)
                .transform(RoundedCorners(dpToPx(4f, itemView.context)))
                .placeholder(R.drawable.poster)
                .into(artWorkUrl)                    // Было: artwork

            // Обработчики кликов
            buttonPlayer.setOnClickListener { onTrackClick(track) }
            itemView.setOnClickListener { onTrackClick(track) }
        }
    }
}