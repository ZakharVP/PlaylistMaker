package com.practicum.playlistmaker.domain

import com.practicum.playlistmaker.domain.models.Track

interface OnTrackClickListener {

    fun onTrackClick(track: Track)
}