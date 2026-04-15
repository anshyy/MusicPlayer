package com.example.musicplayer

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    private lateinit var tvSongName: TextView
    private lateinit var tvCurrentTime: TextView
    private lateinit var tvTotalTime: TextView
    private lateinit var tvSongPosition: TextView
    private lateinit var btnPlay: ImageButton
    private lateinit var btnNext: ImageButton
    private lateinit var btnPrev: ImageButton
    private lateinit var btnShuffle: ImageButton
    private lateinit var btnRepeat: ImageButton
    private lateinit var btnLike: ImageButton
    private lateinit var seekBar: SeekBar

    private val handler = Handler(Looper.getMainLooper())
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        tvSongName = findViewById(R.id.tvSongName)
        tvCurrentTime = findViewById(R.id.tvCurrentTime)
        tvTotalTime = findViewById(R.id.tvTotalTime)
        tvSongPosition = findViewById(R.id.tvSongPosition)
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
            // Toggle like
            val currentColor = btnLike.colorFilter
            val newColor = if (currentColor == null) {
                android.graphics.Color.parseColor("#FF69B4")
            } else {
                android.graphics.Color.parseColor("#B3B3B3")
            }
            btnLike.setColorFilter(newColor)
        }

        MusicPlayerManager.onSongChanged = {
            updateUI()
        }

        MusicPlayerManager.onPlaybackStatusChanged = { isPlaying ->
            btnPlay.setImageResource(if (isPlaying) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play)
        }

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
        tvSongName.text = MusicPlayerManager.getCurrentSongName()
        val total = MusicPlayerManager.currentSongList.size
        val current = MusicPlayerManager.currentPosition + 1
        tvSongPosition.text = String.format(Locale.getDefault(), "%d / %d", current, total)

        MusicPlayerManager.getMediaPlayer()?.let {
            seekBar.max = it.duration
            tvTotalTime.text = formatTime(it.duration)
            btnPlay.setImageResource(if (it.isPlaying) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play)
        }
        updateToggleColors()
    }

    private fun updateToggleColors() {
        val activeColor = android.graphics.Color.parseColor("#1DB954")
        val inactiveColor = android.graphics.Color.parseColor("#B3B3B3")

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
        handler.removeCallbacks(updateSeekBar)
    }
}
