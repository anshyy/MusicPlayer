package com.example.musicplayer

import android.media.MediaPlayer
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class PlayerActivity : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null
    private var currentPosition = 0
    private lateinit var songPaths: ArrayList<String>
    private lateinit var songNames: ArrayList<String>

    private lateinit var tvSongName: TextView
    private lateinit var btnPlay: ImageButton
    private lateinit var btnNext: ImageButton
    private lateinit var btnPrev: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        // Get data passed from MainActivity
        songPaths = intent.getStringArrayListExtra("songPaths") ?: arrayListOf()
        songNames = intent.getStringArrayListExtra("songNames") ?: arrayListOf()
        currentPosition = intent.getIntExtra("position", 0)

        tvSongName = findViewById(R.id.tvSongName)
        btnPlay    = findViewById(R.id.btnPlay)
        btnNext    = findViewById(R.id.btnNext)
        btnPrev    = findViewById(R.id.btnPrev)

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
            currentPosition = (currentPosition + 1) % songPaths.size
            playSong(currentPosition)
        }

        btnPrev.setOnClickListener {
            currentPosition = if (currentPosition - 1 < 0) songPaths.size - 1
            else currentPosition - 1
            playSong(currentPosition)
        }
    }

    private fun playSong(position: Int) {
        mediaPlayer?.stop()
        mediaPlayer?.release()

        tvSongName.text = songNames[position]
        mediaPlayer = MediaPlayer().apply {
            setDataSource(songPaths[position])
            prepare()
            start()
        }
        btnPlay.setImageResource(android.R.drawable.ic_media_pause)

        // Auto play next when song finishes
        mediaPlayer?.setOnCompletionListener {
            currentPosition = (currentPosition + 1) % songPaths.size
            playSong(currentPosition)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
