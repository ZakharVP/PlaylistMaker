package com.practicum.playlistmaker.playlist.search.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.practicum.playlistmaker.databinding.CardTrackBinding
import com.practicum.playlistmaker.playlist.sharing.data.models.Track

class TrackAdapter(
    private val onTrackClick: (Track) -> Unit
) : ListAdapter<Track, TrackViewHolder>(TrackDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val binding = CardTrackBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TrackViewHolder(binding, onTrackClick)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}