package com.practicum.playlistmaker.playlist.mediateka.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.databinding.ItemPlaylistBinding
import com.practicum.playlistmaker.playlist.sharing.data.models.Playlist
import com.bumptech.glide.Glide
import com.practicum.playlistmaker.R

class PlaylistViewHolder(
    private val binding: ItemPlaylistBinding,
    private val onPlaylistClick: (Playlist) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(playlist: Playlist) {
        binding.playlistName.text = playlist.name

        // Используйте правильное имя поля (tracksCount вместо trackCount)
        binding.trackCount.text = itemView.context.resources.getQuantityString(
            R.plurals.tracks_count,
            playlist.tracksCount, // Исправлено на tracksCount
            playlist.tracksCount  // Исправлено на tracksCount
        )

        // Загрузка изображения обложки - используйте coverUri
        if (!playlist.coverUri.isNullOrEmpty()) {
            Glide.with(itemView.context)
                .load(playlist.coverUri)
                .placeholder(R.drawable.no_image_placeholder)
                .into(binding.playlistImage)
        } else {
            binding.playlistImage.setImageResource(R.drawable.no_image_placeholder)
        }

        binding.root.setOnClickListener {
            onPlaylistClick(playlist)
        }
    }
}