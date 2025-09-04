package com.practicum.playlistmaker.playlist.player.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.playlist.mediateka.ui.adapters.PlaylistDiffCallback
import com.practicum.playlistmaker.playlist.sharing.data.models.Playlist

class PlaylistBottomSheetAdapter(
    private val onPlaylistClick: (Playlist) -> Unit
) : ListAdapter<Playlist, PlaylistBottomSheetViewHolder>(PlaylistDiffCallback()) {

    fun updateList(newList: List<Playlist>) {
        submitList(newList.toList()) // Создаем новый список
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistBottomSheetViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_playlist_bottom_sheet, parent, false)
        return PlaylistBottomSheetViewHolder(view, onPlaylistClick)
    }

    override fun onBindViewHolder(holder: PlaylistBottomSheetViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}