package com.example.musicplayer

import android.net.Uri

data class Playlist(
    val name: String,
    val imageUri: Uri? = null,
    val songPaths: List<String> = emptyList(),
    val color: String = "#8B5FBF"  // Purple accent
)
