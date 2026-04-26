package com.example.musicplayer

import android.net.Uri

data class DailyMix(
    val id: String,
    val title: String,
    val description: String,
    val imageUri: Uri? = null,
    val color: String,
    val songs: List<Song> = emptyList()
)

