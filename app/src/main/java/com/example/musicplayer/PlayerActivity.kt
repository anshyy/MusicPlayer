package com.example.musicplayer

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null
    private var currentPosition = 0
    private lateinit var songPaths: ArrayList<String>
    private lateinit var songNames: ArrayList<String>

    private lateinit var tvSongName: TextView
    private lateinit var tvCurrentTime: TextView
    private lateinit var tvTotalTime: TextView
    private lateinit var btnPlay: ImageButton
    private lateinit var btnNext: ImageButton
    private lateinit var btnPrev: ImageButton
    private lateinit var seekBar: SeekBar

    private val handler = Handler(Looper.getMainLooper())
    private val updateSeekBar = object : Runnable {
        override fun run() {
            mediaPlayer?.let {
                seekBar.progress = it.currentPosition
                tvCurrentTime.text = formatTime(it.currentPosition)
            }
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        // Get data passed from MainActivity
        songPaths = intent.getStringArrayListExtra("songPaths") ?: arrayListOf()
        songNames = intent.getStringArrayListExtra("songNames") ?: arrayListOf()
        currentPosition = intent.getIntExtra("position", 0)

        tvSongName = findViewById(R.id.tvSongName)
        tvCurrentTime = findViewById(R.id.tvCurrentTime)
        tvTotalTime = findViewById(R.id.tvTotalTime)
        btnPlay    = findViewById(R.id.btnPlay)
        btnNext    = findViewById(R.id.btnNext)
        btnPrev    = findViewById(R.id.btnPrev)
        seekBar    = findViewById(R.id.seekBar)

        playSong(currentPosition)

        btnPlay.setOnClickListener {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
                btnPlay.setImageResource(android.R.drawable.ic_media_play)
            } else {
                mediaPlayer?.start()
                btnPlay.setImageResource(android.R.drawable.ic_media_pause)
            }
        }

        btnNext.setOnClickListener {
            if (songPaths.isNotEmpty()) {
                currentPosition = (currentPosition + 1) % songPaths.size
                playSong(currentPosition)
            }
        }

        btnPrev.setOnClickListener {
            if (songPaths.isNotEmpty()) {
                currentPosition = if (currentPosition - 1 < 0) songPaths.size - 1
                else currentPosition - 1
                playSong(currentPosition)
            }
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer?.seekTo(progress)
                    tvCurrentTime.text = formatTime(progress)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun playSong(position: Int) {
        if (songPaths.isEmpty()) return

        mediaPlayer?.stop()
        mediaPlayer?.release()

        tvSongName.text = songNames[position]
        mediaPlayer = MediaPlayer().apply {
            setDataSource(songPaths[position])
            prepare()
            start()
            seekBar.max = duration
            tvTotalTime.text = formatTime(duration)
        }
        btnPlay.setImageResource(android.R.drawable.ic_media_pause)

        // Auto play next when song finishes
        mediaPlayer?.setOnCompletionListener {
            currentPosition = (currentPosition + 1) % songPaths.size
            playSong(currentPosition)
        }

        handler.post(updateSeekBar)
    }

    private fun formatTime(milliseconds: Int): String {
        val minutes = (milliseconds / 1000) / 60
        val seconds = (milliseconds / 1000) % 60
        return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateSeekBar)
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}

