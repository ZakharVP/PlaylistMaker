package com.practicum.playlistmaker.presentation.adapters

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.models.Track

class TrackViewHolder (itemView: View): RecyclerView.ViewHolder(itemView) {

    private val sourceNameTrack: TextView = itemView.findViewById(R.id.name_track)
    private val sourceNameSinger: TextView = itemView.findViewById(R.id.name_track_singers)
    private val sourceDuration: TextView = itemView.findViewById(R.id.track_duration)
    private val sourceUrlPoster: ImageView = itemView.findViewById(R.id.art_work_url)
    private val sourcePlaceholder = R.drawable.no_image_placeholder

    fun bind(model: Track) {

        val imageUrl = model.artworkUrl
        Glide.with(itemView)
            .load(imageUrl)
            .placeholder(sourcePlaceholder)
            .transform(RoundedCorners(dpToPx(2f,itemView.context)))
            .into(sourceUrlPoster)

        sourceNameTrack.text = model.trackName
        sourceNameSinger.text = model.artistName
        //sourceDuration.text = model.formatDuration()

    }

    fun dpToPx(dp: Float, context: Context): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
    }
}