package com.practicum.playlistmaker

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.Activity.AudioPlayer
import com.practicum.playlistmaker.Activity.MainActivity

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

        //Здесь реагирует вся карточка целиком
        holder.itemView.setOnClickListener{
            val track = dataTrack[position]
            TrackManager.saveTrackToPreferences(context, track)
            val message = "Трек добавлен в историю: ${track.trackName}"
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
        holder.itemView.findViewById<Button>(R.id.buttonPlayer).setOnClickListener{
            val track = dataTrack[position]
            val displayIntent = Intent(context, AudioPlayer::class.java).apply {
                putExtra("trackId",track.trackId)
                putExtra("trackName",track.trackName)
                putExtra("trackTimeMillis",track.trackTimeMillis)
                putExtra("artistName",track.artistName)
                putExtra("artworkUrl100",track.artworkUrl100)

                putExtra("collectionName",track.collectionName)
                //putExtra("trackYear",track.trackYear)
                putExtra("primaryGenreName",track.primaryGenreName)
                putExtra("country",track.country)
            }
            context.startActivity(displayIntent)
        }

    }

    override fun getItemCount(): Int {
        return dataTrack.size
    }

}

