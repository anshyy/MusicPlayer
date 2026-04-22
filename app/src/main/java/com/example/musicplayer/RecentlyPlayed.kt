package com.example.musicplayer

data class RecentlyPlayed(
    val songName: String,
    val songPath: String,
    val playedAt: Long = System.currentTimeMillis()
)

