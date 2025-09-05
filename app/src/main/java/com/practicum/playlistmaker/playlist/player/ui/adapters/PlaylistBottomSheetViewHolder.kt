package com.practicum.playlistmaker.playlist.player.ui.adapters

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.playlist.sharing.data.models.Playlist

class PlaylistBottomSheetViewHolder(
    itemView: View,
    private val onPlaylistClick: (Playlist) -> Unit
) : RecyclerView.ViewHolder(itemView) {

    private val playlistImage: ImageView = itemView.findViewById(R.id.playlistImage)
    private val playlistName: TextView = itemView.findViewById(R.id.playlistName)
    private val tracksCount: TextView = itemView.findViewById(R.id.tracksCount)

    fun bind(playlist: Playlist) {
        playlistName.text = playlist.name
        tracksCount.text = itemView.context.resources.getQuantityString(
            R.plurals.tracks_count,
            playlist.tracksCount,
            playlist.tracksCount
        )

        if (!playlist.coverUri.isNullOrEmpty()) {
            Glide.with(itemView.context)
                .load(playlist.coverUri)
                .placeholder(R.drawable.no_image_placeholder)
                .into(playlistImage)
        } else {
            playlistImage.setImageResource(R.drawable.no_image_placeholder)
        }

        itemView.setOnClickListener {
            onPlaylistClick(playlist)
        }
    }
}