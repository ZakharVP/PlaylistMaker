package com.practicum.playlistmaker.playlist.mediateka.ui.adapters

import androidx.recyclerview.widget.DiffUtil
import com.practicum.playlistmaker.playlist.sharing.data.models.Playlist

class PlaylistDiffCallback : DiffUtil.ItemCallback<Playlist>() {
    override fun areItemsTheSame(oldItem: Playlist, newItem: Playlist): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Playlist, newItem: Playlist): Boolean {
        return oldItem == newItem
    }
}