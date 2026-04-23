package com.example.musicplayer

import android.net.Uri

data class RecentlyPlayed(
    val songName: String,
    val songPath: String,
    val playedAt: Long = System.currentTimeMillis(),
    val albumArtUri: Uri? = null
)

