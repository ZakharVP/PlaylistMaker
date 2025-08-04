package com.practicum.playlistmaker.playlist.mediateka.ui.adapters

import com.practicum.playlistmaker.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.databinding.CardTrackBinding
import com.practicum.playlistmaker.playlist.sharing.data.models.Track
import com.practicum.playlistmaker.util.dpToPx

class FavoritesViewHolder(
    private val binding: CardTrackBinding,
    private val onTrackClick: (Track) -> Unit
) : androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {

    fun bind(track: Track) {
        with(binding) {
            nameTrack.text = track.trackName
            nameTrackSingers.text = track.artistName
            trackDuration.text = track.trackTimeMillisString

            Glide.with(itemView)
                .load(track.artworkUrl)
                .transform(RoundedCorners(dpToPx(4f, itemView.context)))
                .placeholder(R.drawable.poster)
                .into(artWorkUrl)

            buttonPlayer.setOnClickListener { onTrackClick(track) }
            itemView.setOnClickListener { onTrackClick(track) }
        }
    }
}