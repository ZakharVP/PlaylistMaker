package com.practicum.playlistmaker

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class TrackViewHolder (itemView: View): RecyclerView.ViewHolder(itemView) {

    private val sourceNameTrack: TextView = itemView.findViewById(R.id.name_track)
    private val sourceNameSinger: TextView = itemView.findViewById(R.id.name_track_singers)
    private val sourceDuration: TextView = itemView.findViewById(R.id.track_duration)
    private val sourceUrlPoster: ImageView = itemView.findViewById(R.id.art_work_url)
    private val sourcePlaceholder = R.drawable.no_image_placeholder

    fun bind(model: Track) {

        val imageUrl = model.artWorkUrl100
        Glide.with(itemView)
            .load(imageUrl)
            .placeholder(sourcePlaceholder)
            .into(sourceUrlPoster)

        sourceNameTrack.text = model.trackName
        sourceNameSinger.text = model.artistName
        sourceDuration.text = model.trackTime

    }
}