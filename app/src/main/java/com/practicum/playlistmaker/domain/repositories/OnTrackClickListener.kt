package com.practicum.playlistmaker.domain.repositories

import com.practicum.playlistmaker.domain.models.Track

interface OnTrackClickListener {

    fun onTrackClick(track: Track)
}