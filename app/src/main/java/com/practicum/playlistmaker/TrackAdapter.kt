package com.practicum.playlistmaker

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class TrackAdapter (
    private val context: Context,
    private val dataTrack: List<Track>
) : RecyclerView.Adapter<TrackViewHolder> () {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_track, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(dataTrack[position])
        holder.itemView.setOnClickListener{
            val track = dataTrack[position]
            TrackManager.saveTrackToPreferences(context, track)
            val message = "Трек добавлен в историю: ${track.trackName}"
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return dataTrack.size
    }

}

