package com.example.musicplayer

import android.content.Context
import android.media.MediaPlayer
import android.media.audiofx.LoudnessEnhancer
import android.net.Uri
import android.os.Handler
import android.os.Looper

object MusicPlayerManager {
    private var mediaPlayer: MediaPlayer? = null
    private var nextMediaPlayer: MediaPlayer? = null
    private var loudnessEnhancer: LoudnessEnhancer? = null
    private val handler = Handler(Looper.getMainLooper())
    private var sleepTimerRunnable: Runnable? = null

    var currentSongList: List<String> = emptyList()
    var currentSongPaths: List<String> = emptyList()
    var currentSongArtUris: List<Uri?> = emptyList()
    var currentArtists: List<String> = emptyList()
    var currentPosition: Int = -1
    
    // Settings
    var isShuffle: Boolean = false
    var isRepeat: Boolean = false
    var isGapless: Boolean = false
    var isNormalization: Boolean = false
    var crossfadeDuration: Int = 0 // in seconds

    // Liked Songs
    val likedSongPaths = HashSet<String>()
    private val likedSongsListeners = mutableListOf<() -> Unit>()

    fun toggleLike(path: String) {
        if (likedSongPaths.contains(path)) {
            likedSongPaths.remove(path)
        } else {
            likedSongPaths.add(path)
        }
        notifyLikedSongsChanged()
    }

    fun isLiked(path: String): Boolean = likedSongPaths.contains(path)

    fun addOnLikedSongsChangedListener(listener: () -> Unit) {
        if (!likedSongsListeners.contains(listener)) likedSongsListeners.add(listener)
    }

    fun removeOnLikedSongsChangedListener(listener: () -> Unit) {
        likedSongsListeners.remove(listener)
    }

    private fun notifyLikedSongsChanged() {
        likedSongsListeners.forEach { it.invoke() }
    }

    private val songChangedListeners = mutableListOf<(Int) -> Unit>()
    private val playbackStatusListeners = mutableListOf<(Boolean) -> Unit>()

    fun addOnSongChangedListener(listener: (Int) -> Unit) {
        if (!songChangedListeners.contains(listener)) songChangedListeners.add(listener)
    }

    fun removeOnSongChangedListener(listener: (Int) -> Unit) {
        songChangedListeners.remove(listener)
    }

    fun addOnPlaybackStatusChangedListener(listener: (Boolean) -> Unit) {
        if (!playbackStatusListeners.contains(listener)) playbackStatusListeners.add(listener)
    }

    fun removeOnPlaybackStatusChangedListener(listener: (Boolean) -> Unit) {
        playbackStatusListeners.remove(listener)
    }

    private fun notifySongChanged(position: Int) {
        songChangedListeners.forEach { it.invoke(position) }
    }

    private fun notifyPlaybackStatusChanged(isPlaying: Boolean) {
        playbackStatusListeners.forEach { it.invoke(isPlaying) }
    }

    fun playSong(context: Context, position: Int) {
        if (currentSongPaths.isEmpty() || position < 0 || position >= currentSongPaths.size) return
        
        currentPosition = position
        
        // Clean up current players
        mediaPlayer?.stop()
        mediaPlayer?.release()
        nextMediaPlayer?.release()
        nextMediaPlayer = null
        loudnessEnhancer?.release()

        mediaPlayer = createPlayer(context, position)
        if (mediaPlayer == null) {
            notifyPlaybackStatusChanged(false)
            return
        }
        
        mediaPlayer?.start()
        
        applyEffects()
        
        if (isGapless) {
            prepareNextPlayer(context)
        }

        notifySongChanged(position)
        notifyPlaybackStatusChanged(true)
    }

    private fun createPlayer(context: Context, position: Int): MediaPlayer? {
        if (position < 0 || position >= currentSongPaths.size) return null
        val path = currentSongPaths[position]
        
        return try {
            MediaPlayer().apply {
                val uri = Uri.parse(path)
                if (path.startsWith("mock_path_")) {
                    // For mock paths, we can't really play them, but let's not crash.
                    // Ideally we'd have a local raw resource as fallback.
                    // For now, let's just throw to trigger the catch block.
                    throw java.io.FileNotFoundException("Mock path: $path")
                }
                setDataSource(context, uri)
                prepare()
                isLooping = isRepeat && !isGapless
                setOnCompletionListener {
                    if (isGapless && nextMediaPlayer != null) {
                        // Transition to next player
                        mediaPlayer?.release()
                        mediaPlayer = nextMediaPlayer
                        nextMediaPlayer = null
                        
                        val nextPos = getNextPosition()
                        this@MusicPlayerManager.currentPosition = nextPos
                        
                        if (mediaPlayer?.isPlaying == false) {
                            mediaPlayer?.start()
                        }
                        applyEffects()
                        prepareNextPlayer(context)
                        notifySongChanged(currentPosition)
                    } else {
                        playNext(context)
                    }
                }
                setOnErrorListener { _, _, _ -> true }
            }
        } catch (e: Exception) {
            android.util.Log.e("MusicPlayerManager", "Error creating player for $path: ${e.message}")
            null
        }
    }

    private fun prepareNextPlayer(context: Context) {
        val nextPos = getNextPosition()
        if (nextPos != -1) {
            nextMediaPlayer = createPlayer(context, nextPos)
            if (nextMediaPlayer != null) {
                mediaPlayer?.setNextMediaPlayer(nextMediaPlayer)
            }
        }
    }

    private fun getNextPosition(): Int {
        if (currentSongPaths.isEmpty()) return -1
        return if (isShuffle) {
            (currentSongPaths.indices).random()
        } else {
            (currentPosition + 1) % currentSongPaths.size
        }
    }

    private fun applyEffects() {
        mediaPlayer?.let { player ->
            loudnessEnhancer?.release()
            if (isNormalization) {
                try {
                    loudnessEnhancer = LoudnessEnhancer(player.audioSessionId).apply {
                        setTargetGain(1000) // Increase loudness by 10dB
                        enabled = true
                    }
                } catch (e: Exception) {}
            } else {
                loudnessEnhancer = null
            }
        }
    }

    fun updateNormalization(enabled: Boolean) {
        isNormalization = enabled
        applyEffects()
    }

    fun setSleepTimer(minutes: Int, onTimerFinished: () -> Unit) {
        sleepTimerRunnable?.let { handler.removeCallbacks(it) }
        
        if (minutes > 0) {
            sleepTimerRunnable = Runnable {
                pause()
                onTimerFinished()
            }
            handler.postDelayed(sleepTimerRunnable!!, minutes * 60 * 1000L)
        } else {
            sleepTimerRunnable = null
        }
    }

    fun playNext(context: Context) {
        val nextPos = getNextPosition()
        if (nextPos != -1) playSong(context, nextPos)
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
                notifyPlaybackStatusChanged(false)
            } else {
                it.start()
                notifyPlaybackStatusChanged(true)
            }
        }
    }

    fun pause() {
        mediaPlayer?.pause()
        notifyPlaybackStatusChanged(false)
    }

    fun getMediaPlayer(): MediaPlayer? = mediaPlayer

    fun getCurrentSongName(): String? {
        return if (currentPosition in currentSongList.indices) currentSongList[currentPosition] else null
    }

    fun getCurrentArtist(): String? {
        return if (currentPosition in currentArtists.indices) currentArtists[currentPosition] else null
    }

    fun getCurrentSongArtUri(): Uri? {
        return if (currentPosition in currentSongArtUris.indices) currentSongArtUris[currentPosition] else null
    }

    fun release() {
        mediaPlayer?.release()
        nextMediaPlayer?.release()
        loudnessEnhancer?.release()
        mediaPlayer = null
        nextMediaPlayer = null
        loudnessEnhancer = null
    }
}
