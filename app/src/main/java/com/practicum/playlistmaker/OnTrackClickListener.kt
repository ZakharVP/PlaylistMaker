package com.practicum.playlistmaker

import com.practicum.playlistmaker.domain.Track

interface OnTrackClickListener {
    fun onTrackClick(track: Track)
}