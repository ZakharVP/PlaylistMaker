package com.practicum.playlistmaker.domain.models

import android.content.ContentValues.TAG
import android.util.Log
import com.practicum.playlistmaker.data.dto.TrackDto

data class Track (

    val trackName: String,
    val artistName: String,
    val trackTimeMillisString: String,
    val trackId: String,
    val artworkUrl: String,
    val collectionName: String?,
    val releaseYear: String,
    val genre: String,
    val country: String,
    val previewUrl: String

) {
    companion object {

        fun fromDto(dto: TrackDto): Track? {

            // Проверяем обязательные поля
            if (dto.trackName == null ||
                dto.artistName == null ||
                dto.trackTimeMillis == null ||
                dto.trackId == null ||
                dto.artworkUrl100 == null ||
                dto.previewUrl == null) {
                return null
            }

            return try {
                Track(
                    trackName = dto.trackName,
                    artistName = dto.artistName,
                    trackTimeMillisString = formatDuration(dto.trackTimeMillis),
                    trackId = dto.trackId.toString(),
                    artworkUrl = dto.artworkUrl100.replace("100x100", "600x600"),
                    collectionName = dto.collectionName,
                    releaseYear = dto.releaseDate?.take(4) ?: "",
                    genre = dto.primaryGenreName ?: "",
                    country = dto.country ?: "",
                    previewUrl = dto.previewUrl
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


/*
Напоминалка по всем ключам
{
    "wrapperType":"track",
    "kind":"song",
    "artistId":909253,
    "collectionId":120954021,
    "trackId":120954025,
    "artistName":"Jack Johnson",
    "collectionName":"Sing-a-Longs and Lullabies for the Film Curious George",
    "trackName":"Upside Down",
    "collectionCensoredName":"Sing-a-Longs and Lullabies for the Film Curious George", "trackCensoredName":"Upside Down",
    "artistViewUrl":"https://itunes.apple.com/WebObjects/MZStore.woa/wa/viewArtist?id=909253",
    "collectionViewUrl":"https://itunes.apple.com/WebObjects/MZStore.woa/wa/viewAlbum?i=120954025&id=120954021&s=143441",
    "trackViewUrl":"https://itunes.apple.com/WebObjects/MZStore.woa/wa/viewAlbum?i=120954025&id=120954021&s=143441",
    "previewUrl":"http://a1099.itunes.apple.com/r10/Music/f9/54/43/mzi.gqvqlvcq.aac.p.m4p",
    "artworkUrl60":"http://a1.itunes.apple.com/r10/Music/3b/6a/33/mzi.qzdqwsel.60x60-50.jpg",
    "artworkUrl100":"http://a1.itunes.apple.com/r10/Music/3b/6a/33/mzi.qzdqwsel.100x100-75.jpg",
    "collectionPrice":10.99,
    "trackPrice":0.99,
    "collectionExplicitness":"notExplicit",
    "trackExplicitness":"notExplicit",
    "discCount":1,
    "discNumber":1,
    "trackCount":14,
    "trackNumber":1,
    "trackTimeMillis":210743,
    "country":"USA",
    "currency":"USD",
    "primaryGenreName":"Rock"
}
*/
