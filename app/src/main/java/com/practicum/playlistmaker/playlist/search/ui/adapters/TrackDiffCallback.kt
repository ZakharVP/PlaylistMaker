package com.practicum.playlistmaker.playlist.search.ui.adapters

import androidx.recyclerview.widget.DiffUtil
import com.practicum.playlistmaker.playlist.sharing.data.models.Track

class TrackDiffCallback : DiffUtil.ItemCallback<Track>() {
    override fun areItemsTheSame(oldItem: Track, newItem: Track): Boolean {
        return oldItem.trackId == newItem.trackId
    }

    override fun areContentsTheSame(oldItem: Track, newItem: Track): Boolean {
        return oldItem == newItem
    }
}