package com.practicum.playlistmaker

import kotlinx.coroutines.FlowPreview
import retrofit2.http.Url

data class Track (

    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val trackTimeMillisString: String,
    val trackId: String,
    val artworkUrl100: String,
    val collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String

) {
    fun getDuration(): String {
        val totalSeconds = trackTimeMillis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
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
