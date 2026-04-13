package com.example.musicplayer

import android.net.Uri

data class Playlist(
    val name: String,
    val imageUri: Uri? = null,
    val songPaths: List<String> = emptyList()
)
