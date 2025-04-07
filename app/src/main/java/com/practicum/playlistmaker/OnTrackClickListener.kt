package com.practicum.playlistmaker

import com.practicum.playlistmaker.data.Track

interface OnTrackClickListener {
    fun onTrackClick(track: Track)
}