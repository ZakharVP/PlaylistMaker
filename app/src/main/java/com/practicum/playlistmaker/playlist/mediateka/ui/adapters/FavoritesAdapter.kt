package com.practicum.playlistmaker.playlist.mediateka.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.practicum.playlistmaker.databinding.CardTrackBinding
import com.practicum.playlistmaker.playlist.sharing.ui.adapters.TrackDiffCallback
import com.practicum.playlistmaker.playlist.sharing.data.models.Track

class FavoritesAdapter(
    private val onTrackClick: (Track) -> Unit
) : ListAdapter<Track, FavoritesViewHolder>(TrackDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritesViewHolder {
        val binding = CardTrackBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FavoritesViewHolder(binding, onTrackClick)
    }

    override fun onBindViewHolder(holder: FavoritesViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}