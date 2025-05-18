package com.practicum.playlistmaker.presentation.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.data.sharedPreferences.TrackManager
import com.practicum.playlistmaker.domain.OnTrackClickListener

class TrackAdapter (
    private var tracks: List<Track>,
    private val listener: OnTrackClickListener
) : RecyclerView.Adapter<TrackViewHolder> () {

    fun updateTracks(newTracks: List<Track>) {
        tracks = newTracks
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_track, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = tracks[position]
        holder.bind(track)

        holder.itemView.setOnClickListener {
            listener.onTrackClick(track)
        }
    }

    override fun getItemCount(): Int = tracks.size

}

