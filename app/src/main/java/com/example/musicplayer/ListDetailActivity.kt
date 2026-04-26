package com.example.musicplayer

import android.content.Intent
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
        val songs = intent.getParcelableArrayListExtra<Song>("songs") ?: arrayListOf()

        findViewById<TextView>(R.id.tvListTitle).text = title
        
        val rvSongs = findViewById<RecyclerView>(R.id.rvListSongs)
        rvSongs.layoutManager = LinearLayoutManager(this)
        
        val adapter = SongAdapter(songs, HashSet<String>(), { position ->
            MusicPlayerManager.currentSongList = songs.map { it.title }
            MusicPlayerManager.currentSongPaths = songs.map { it.path }
            MusicPlayerManager.playSong(this, position)
            startActivity(Intent(this, PlayerActivity::class.java))
        }, { _, _ -> 
            // Handle like if needed
        })
        rvSongs.adapter = adapter

        findViewById<ImageButton>(R.id.btnBackDetail).setOnClickListener {
            finish()
        }

        findViewById<androidx.cardview.widget.CardView>(R.id.btnPlayAll).setOnClickListener {
            if (songs.isNotEmpty()) {
                MusicPlayerManager.currentSongList = songs.map { it.title }
                MusicPlayerManager.currentSongPaths = songs.map { it.path }
                MusicPlayerManager.playSong(this, 0)
                startActivity(Intent(this, PlayerActivity::class.java))
            }
        }
    }
}
