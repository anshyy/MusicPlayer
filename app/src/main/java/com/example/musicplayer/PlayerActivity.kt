package com.example.musicplayer

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    private lateinit var tvSongName: TextView
    private lateinit var tvArtistName: TextView
    private lateinit var ivAlbumArt: ImageView
    private lateinit var tvCurrentTime: TextView
    private lateinit var tvTotalTime: TextView
    private lateinit var btnPlay: ImageButton
    private lateinit var btnNext: ImageButton
    private lateinit var btnPrev: ImageButton
    private lateinit var btnShuffle: ImageButton
    private lateinit var btnRepeat: ImageButton
    private lateinit var btnLike: ImageButton
    private lateinit var seekBar: SeekBar

    private val handler = Handler(Looper.getMainLooper())
    
    private val songChangedListener: (Int) -> Unit = {
        updateUI()
    }

    private val playbackStatusListener: (Boolean) -> Unit = { isPlaying ->
        btnPlay.setImageResource(if (isPlaying) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play)
        updateUI() // To handle potential background color updates or scaling if needed later
    }

    private val updateSeekBar = object : Runnable {
        override fun run() {
            MusicPlayerManager.getMediaPlayer()?.let {
                if (it.isPlaying) {
                    seekBar.progress = it.currentPosition
                    tvCurrentTime.text = formatTime(it.currentPosition)
                }
            }
            handler.postDelayed(this, 1000)
        }
    }

    private val likedSongsChangeListener: () -> Unit = {
        updateLikeButtonUI()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        tvSongName = findViewById(R.id.tvSongName)
        tvArtistName = findViewById(R.id.tvArtistName)
        ivAlbumArt = findViewById(R.id.ivAlbumArt)
        tvCurrentTime = findViewById(R.id.tvCurrentTime)
        tvTotalTime = findViewById(R.id.tvTotalTime)
        btnPlay    = findViewById(R.id.btnPlay)
        btnNext    = findViewById(R.id.btnNext)
        btnPrev    = findViewById(R.id.btnPrev)
        btnShuffle = findViewById(R.id.btnShuffle)
        btnRepeat  = findViewById(R.id.btnRepeat)
        btnLike    = findViewById(R.id.btnLike)
        seekBar    = findViewById(R.id.seekBar)

        updateUI()

        btnPlay.setOnClickListener {
            MusicPlayerManager.togglePlayPause()
        }

        btnNext.setOnClickListener {
            MusicPlayerManager.playNext(this)
        }

        btnPrev.setOnClickListener {
            MusicPlayerManager.playPrevious(this)
        }

        btnShuffle.setOnClickListener {
            MusicPlayerManager.isShuffle = !MusicPlayerManager.isShuffle
            updateToggleColors()
        }

        btnRepeat.setOnClickListener {
            MusicPlayerManager.isRepeat = !MusicPlayerManager.isRepeat
            MusicPlayerManager.getMediaPlayer()?.isLooping = MusicPlayerManager.isRepeat
            updateToggleColors()
        }

        btnLike.setOnClickListener {
            val currentPath = MusicPlayerManager.currentSongPaths.getOrNull(MusicPlayerManager.currentPosition)
            currentPath?.let { path ->
                MusicPlayerManager.toggleLike(path)
            }
        }

        MusicPlayerManager.addOnSongChangedListener(songChangedListener)
        MusicPlayerManager.addOnPlaybackStatusChangedListener(playbackStatusListener)
        MusicPlayerManager.addOnLikedSongsChangedListener(likedSongsChangeListener)

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    MusicPlayerManager.getMediaPlayer()?.seekTo(progress)
                    tvCurrentTime.text = formatTime(progress)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        handler.post(updateSeekBar)
    }

    private fun updateUI() {
        if (isFinishing || isDestroyed) return
        
        tvSongName.text = MusicPlayerManager.getCurrentSongName()
        tvArtistName.text = MusicPlayerManager.getCurrentArtist() ?: "Unknown Artist"
        
        Glide.with(this)
            .load(MusicPlayerManager.getCurrentSongArtUri())
            .placeholder(R.drawable.gradient_card_pop)
            .error(R.drawable.gradient_card_pop)
            .into(ivAlbumArt)

        MusicPlayerManager.getMediaPlayer()?.let {
            seekBar.max = it.duration
            tvTotalTime.text = formatTime(it.duration)
            btnPlay.setImageResource(if (it.isPlaying) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play)
        }
        updateToggleColors()
        updateLikeButtonUI()
    }

    private fun updateLikeButtonUI() {
        val currentPath = MusicPlayerManager.currentSongPaths.getOrNull(MusicPlayerManager.currentPosition)
        val isLiked = currentPath?.let { MusicPlayerManager.isLiked(it) } ?: false
        
        if (isLiked) {
            btnLike.setColorFilter(androidx.core.content.ContextCompat.getColor(this, R.color.accent_primary))
            btnLike.tag = "liked"
        } else {
            btnLike.setColorFilter(androidx.core.content.ContextCompat.getColor(this, R.color.text_secondary))
            btnLike.tag = "unliked"
        }
    }

    private fun updateToggleColors() {
        val activeColor = androidx.core.content.ContextCompat.getColor(this, R.color.accent_highlight)
        val inactiveColor = androidx.core.content.ContextCompat.getColor(this, R.color.text_tertiary)

        btnShuffle.setColorFilter(if (MusicPlayerManager.isShuffle) activeColor else inactiveColor)
        btnRepeat.setColorFilter(if (MusicPlayerManager.isRepeat) activeColor else inactiveColor)
    }

    private fun formatTime(milliseconds: Int): String {
        val minutes = (milliseconds / 1000) / 60
        val seconds = (milliseconds / 1000) % 60
        return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds)
    }

    override fun onDestroy() {
        super.onDestroy()
        MusicPlayerManager.removeOnSongChangedListener(songChangedListener)
        MusicPlayerManager.removeOnPlaybackStatusChangedListener(playbackStatusListener)
        MusicPlayerManager.removeOnLikedSongsChangedListener(likedSongsChangeListener)
        handler.removeCallbacks(updateSeekBar)
    }
}
