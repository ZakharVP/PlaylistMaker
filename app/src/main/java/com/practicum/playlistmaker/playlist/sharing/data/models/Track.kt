package com.practicum.playlistmaker.playlist.sharing.data.models

import android.os.Parcel
import android.os.Parcelable
import android.util.Log

data class Track(
    val trackName: String,
    val artistName: String,
    val trackTimeMillisString: String,
    val trackId: String,
    val artworkUrl: String,
    val collectionName: String?,
    val releaseYear: String,
    val genre: String,
    val country: String,
    val previewUrl: String,
    val trackTime: String
) : Parcelable {

    fun getBigArtUrl(): String = artworkUrl.replace("100x100bb.jpg", "512x512bb.jpg")

    // Реализация Parcelable вручную
    constructor(parcel: Parcel) : this(
        trackName = parcel.readString() ?: "",
        artistName = parcel.readString() ?: "",
        trackTimeMillisString = parcel.readString() ?: "",
        trackId = parcel.readString() ?: "",
        artworkUrl = parcel.readString() ?: "",
        collectionName = parcel.readString(),
        releaseYear = parcel.readString() ?: "",
        genre = parcel.readString() ?: "",
        country = parcel.readString() ?: "",
        previewUrl = parcel.readString() ?: "",
        trackTime = parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(trackName)
        parcel.writeString(artistName)
        parcel.writeString(trackTimeMillisString)
        parcel.writeString(trackId)
        parcel.writeString(artworkUrl)
        parcel.writeString(collectionName)
        parcel.writeString(releaseYear)
        parcel.writeString(genre)
        parcel.writeString(country)
        parcel.writeString(previewUrl)
        parcel.writeString(trackTime)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        private const val TAG = "TrackMapping"

        @JvmField
        val CREATOR = object : Parcelable.Creator<Track> {
            override fun createFromParcel(parcel: Parcel): Track {
                return Track(parcel)
            }

            override fun newArray(size: Int): Array<Track?> {
                return arrayOfNulls(size)
            }
        }

        fun fromDto(dto: TrackDto): Track? {
            if (dto.trackName == null ||
                dto.artistName == null ||
                dto.trackTimeMillis == null ||
                dto.trackId == null ||
                dto.artworkUrl100 == null ||
                dto.previewUrl == null
            ) {
                Log.w(TAG, "Required fields are missing in TrackDto")
                return null
            }

            return try {
                Track(
                    trackName = dto.trackName,
                    artistName = dto.artistName,
                    trackTimeMillisString = formatDuration(dto.trackTimeMillis),
                    trackId = dto.trackId.toString(),
                    artworkUrl = dto.artworkUrl100.replace("100x100bb", "600x600bb"),
                    collectionName = dto.collectionName,
                    releaseYear = dto.releaseDate?.take(4) ?: "",
                    genre = dto.primaryGenreName ?: "",
                    country = dto.country ?: "",
                    previewUrl = dto.previewUrl,
                    trackTime = dto.trackTime ?: ""
                )
            } catch (e: Exception) {
                Log.e(TAG, "Error mapping track ${dto.trackName}", e)
                null
            }
        }

        private fun formatDuration(millis: Long): String {
            val totalSeconds = millis / 1000
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            return String.format("%02d:%02d", minutes, seconds)
        }
    }
}