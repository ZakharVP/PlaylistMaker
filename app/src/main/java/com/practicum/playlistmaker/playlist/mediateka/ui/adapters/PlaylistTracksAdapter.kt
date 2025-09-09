package com.practicum.playlistmaker.playlist.mediateka.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.practicum.playlistmaker.databinding.CardTrackBinding
import com.practicum.playlistmaker.playlist.sharing.data.models.Track

class PlaylistTracksAdapter(
    private val onTrackClick: (Track) -> Unit,
    private val onTrackLongClick: (Track) -> Unit
) : ListAdapter<Track, PlaylistTrackViewHolder>(TrackDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistTrackViewHolder {
        val binding = CardTrackBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PlaylistTrackViewHolder(binding, onTrackClick, onTrackLongClick)
    }

    override fun onBindViewHolder(holder: PlaylistTrackViewHolder, position: Int) {
        val track = getItem(position)
        holder.bind(track)
    }
}

class TrackDiffCallback : DiffUtil.ItemCallback<Track>() {
    override fun areItemsTheSame(oldItem: Track, newItem: Track): Boolean {
        return oldItem.trackId == newItem.trackId
    }

    override fun areContentsTheSame(oldItem: Track, newItem: Track): Boolean {
        return oldItem == newItem
    }
}