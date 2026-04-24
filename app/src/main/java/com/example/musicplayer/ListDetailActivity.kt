package com.example.musicplayer

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ListDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_detail)

        val title = intent.getStringExtra("list_title") ?: "Music List"
        val songNames = intent.getStringArrayListExtra("song_names") ?: arrayListOf()
        val songPaths = intent.getStringArrayListExtra("song_paths") ?: arrayListOf()

        findViewById<TextView>(R.id.tvListTitle).text = title
        
        val rvSongs = findViewById<RecyclerView>(R.id.rvListSongs)
        rvSongs.layoutManager = LinearLayoutManager(this)
        
        val songs = songNames.indices.map { i ->
            Song(songNames[i], "Unknown Artist", songPaths[i], 0, null)
        }

        val adapter = SongAdapter(songs, HashSet<String>(), { position ->
            MusicPlayerManager.currentSongList = songNames
            MusicPlayerManager.currentSongPaths = songPaths
            MusicPlayerManager.playSong(this, position)
        }, { _, _ -> 
            // Handle like if needed
        })
        rvSongs.adapter = adapter

        findViewById<ImageButton>(R.id.btnBackDetail).setOnClickListener {
            finish()
        }

        findViewById<androidx.cardview.widget.CardView>(R.id.btnPlayAll).setOnClickListener {
            if (songPaths.isNotEmpty()) {
                MusicPlayerManager.currentSongList = songNames
                MusicPlayerManager.currentSongPaths = songPaths
                MusicPlayerManager.playSong(this, 0)
            }
        }
    }
}
