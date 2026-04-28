package com.example.musicplayer

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ListDetailActivity : AppCompatActivity() {

    private val allSongs = ArrayList<Song>()
    private val filteredSongs = ArrayList<Song>()
    private lateinit var rvSongs: RecyclerView
    private lateinit var songNames: ArrayList<String>
    private lateinit var songPaths: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_detail)

        val title = intent.getStringExtra("list_title") ?: "Music List"
        val subtitle = intent.getStringExtra("list_subtitle") ?: ""
        val imageUriString = intent.getStringExtra("list_image")
        val colorHex = intent.getStringExtra("list_color")
        songNames = intent.getStringArrayListExtra("song_names") ?: arrayListOf()
        songPaths = intent.getStringArrayListExtra("song_paths") ?: arrayListOf()

        allSongs.addAll(songNames.indices.map { i ->
            Song(songNames[i], "Various Artists", songPaths[i], 0, null)
        })
        filteredSongs.addAll(allSongs)

        findViewById<TextView>(R.id.tvListTitle).text = title
        findViewById<TextView>(R.id.tvListSubtitle).text = subtitle
        
        val ivHeader = findViewById<ImageView>(R.id.ivDetailHeader)
        val fallbackHeader = VisualUtils.getPremiumImageForSeed(title)
        
        Glide.with(this)
            .load(imageUriString ?: fallbackHeader)
            .placeholder(R.drawable.gradient_card_pop)
            .centerCrop()
            .into(ivHeader)
        
        if (colorHex != null) {
            try {
                findViewById<com.google.android.material.appbar.CollapsingToolbarLayout>(R.id.collapsingToolbar)?.let {
                    it.setContentScrimColor(Color.parseColor(colorHex))
                }
            } catch (e: Exception) {}
        }

        rvSongs = findViewById(R.id.rvListSongs)
        rvSongs.layoutManager = LinearLayoutManager(this)
        
        setupAdapter()

        val etSearch = findViewById<EditText>(R.id.etSearchPlaylist)
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filter(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        findViewById<ImageButton>(R.id.btnBackDetail).setOnClickListener {
            finish()
        }

        findViewById<androidx.cardview.widget.CardView>(R.id.btnPlayAll).setOnClickListener { view ->
            if (filteredSongs.isNotEmpty()) {
                // Click animation
                view.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100).withEndAction {
                    view.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start()
                    
                    MusicPlayerManager.currentSongList = filteredSongs.map { it.title }
                    MusicPlayerManager.currentSongPaths = filteredSongs.map { it.path }
                    MusicPlayerManager.currentArtists = filteredSongs.map { it.artist }
                    MusicPlayerManager.currentSongArtUris = filteredSongs.map { it.albumArtUri }
                    
                    MusicPlayerManager.playSong(this, 0)
                    
                    // Open player activity
                    startActivity(Intent(this, PlayerActivity::class.java))
                }.start()
            }
        }
    }

    private fun setupAdapter() {
        val adapter = SongAdapter(filteredSongs, MusicPlayerManager.likedSongPaths, { position ->
            MusicPlayerManager.currentSongList = filteredSongs.map { it.title }
            MusicPlayerManager.currentSongPaths = filteredSongs.map { it.path }
            MusicPlayerManager.currentArtists = filteredSongs.map { it.artist }
            MusicPlayerManager.currentSongArtUris = filteredSongs.map { it.albumArtUri }
            
            MusicPlayerManager.playSong(this, position)
            startActivity(Intent(this, PlayerActivity::class.java))
        }, { _, _ -> 
            // Liked songs handled
        })
        rvSongs.adapter = adapter
    }

    private fun filter(text: String) {
        filteredSongs.clear()
        if (text.isEmpty()) {
            filteredSongs.addAll(allSongs)
        } else {
            val query = text.lowercase()
            for (song in allSongs) {
                if (song.title.lowercase().contains(query) || song.artist.lowercase().contains(query)) {
                    filteredSongs.add(song)
                }
            }
        }
        rvSongs.adapter?.notifyDataSetChanged()
    }
}
