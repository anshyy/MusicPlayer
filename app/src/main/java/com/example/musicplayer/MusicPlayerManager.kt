package com.example.musicplayer

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri

object MusicPlayerManager {
    private var mediaPlayer: MediaPlayer? = null
    var currentSongList: List<String> = emptyList()
    var currentSongPaths: List<String> = emptyList()
    var currentPosition: Int = -1
    var isShuffle: Boolean = false
    var isRepeat: Boolean = false

    var onSongChanged: ((Int) -> Unit)? = null
    var onPlaybackStatusChanged: ((Boolean) -> Unit)? = null

    fun playSong(context: Context, position: Int) {
        if (currentSongPaths.isEmpty() || position < 0 || position >= currentSongPaths.size) return
        
        currentPosition = position
        mediaPlayer?.stop()
        mediaPlayer?.release()

        mediaPlayer = MediaPlayer().apply {
            setDataSource(context, Uri.parse(currentSongPaths[position]))
            prepare()
            isLooping = isRepeat
            start()
            setOnCompletionListener {
                playNext(context)
            }
        }
        onSongChanged?.invoke(position)
        onPlaybackStatusChanged?.invoke(true)
    }

    fun playNext(context: Context) {
        if (currentSongPaths.isEmpty()) return
        val nextPos = if (isShuffle) {
            (currentSongPaths.indices).random()
        } else {
            (currentPosition + 1) % currentSongPaths.size
        }
        playSong(context, nextPos)
    }

    fun playPrevious(context: Context) {
        if (currentSongPaths.isEmpty()) return
        val prevPos = if (currentPosition - 1 < 0) currentSongPaths.size - 1 else currentPosition - 1
        playSong(context, prevPos)
    }

    fun togglePlayPause() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                onPlaybackStatusChanged?.invoke(false)
            } else {
                it.start()
                onPlaybackStatusChanged?.invoke(true)
            }
        }
    }

    fun getMediaPlayer(): MediaPlayer? = mediaPlayer

    fun getCurrentSongName(): String? {
        return if (currentPosition in currentSongList.indices) currentSongList[currentPosition] else null
    }

    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
