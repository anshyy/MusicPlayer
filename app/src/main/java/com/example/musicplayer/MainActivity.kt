package com.example.musicplayer

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var homeView: android.view.View
    private lateinit var libraryView: android.view.View
    private lateinit var bottomNav: com.google.android.material.bottomnavigation.BottomNavigationView
    
    private val songList = ArrayList<String>()     // song names
    private val songPaths = ArrayList<String>()    // file paths
    private val filteredSongList = ArrayList<String>()
    private val filteredSongPaths = ArrayList<String>()
    private val likedSongPaths = HashSet<String>()
    private val playlists = ArrayList<Playlist>()
    private val permissionCode = 1

    private val createPlaylistLauncher = registerForActivityResult(androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val name = result.data?.getStringExtra("playlist_name") ?: "New Playlist"
            val imageUri = result.data?.getParcelableExtra<android.net.Uri>("playlist_image")
            playlists.add(Playlist(name, imageUri))
            setupLibraryView()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        
        homeView = findViewById(R.id.homeView)
        libraryView = findViewById(R.id.libraryView)
        bottomNav = findViewById(R.id.bottomNavigation)

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    homeView.visibility = android.view.View.VISIBLE
                    libraryView.visibility = android.view.View.GONE
                    true
                }
                R.id.nav_library -> {
                    homeView.visibility = android.view.View.GONE
                    libraryView.visibility = android.view.View.VISIBLE
                    setupLibraryView()
                    true
                }
                else -> false
            }
        }

        findViewById<android.widget.ImageButton>(R.id.btnAddPlaylist).setOnClickListener {
            val intent = Intent(this, PlaylistEditActivity::class.java)
            createPlaylistLauncher.launch(intent)
        }

        // Check and request permission
        if (hasPermission()) {
            loadSongs()
        } else {
            requestPermission()
        }
    }

    private fun hasPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) ==
                    PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_MEDIA_AUDIO), permissionCode)
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), permissionCode)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == permissionCode && grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadSongs()
        } else {
            Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadSongs() {
        // Query device for all audio files
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATA
        )
        val cursor = contentResolver.query(uri, projection, null, null, null)

        cursor?.use {
            while (it.moveToNext()) {
                val title = it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE))
                val path  = it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))
                songList.add(title)
                songPaths.add(path)
            }
        }

        if (songList.isEmpty()) {
            Toast.makeText(this, "No songs found on device", Toast.LENGTH_SHORT).show()
            return
        }

        filteredSongList.addAll(songList)
        filteredSongPaths.addAll(songPaths)

        val adapter = SongAdapter(filteredSongList, { position ->
            val intent = Intent(this, PlayerActivity::class.java)
            intent.putExtra("songPaths", filteredSongPaths)
            intent.putExtra("songNames", filteredSongList)
            intent.putExtra("position", position)
            startActivity(intent)
        }, { position, isLiked ->
            val path = filteredSongPaths[position]
            if (isLiked) likedSongPaths.add(path) else likedSongPaths.remove(path)
            setupLibraryView()
            val status = if (isLiked) "Liked" else "Unliked"
            Toast.makeText(this, "$status: ${filteredSongList[position]}", Toast.LENGTH_SHORT).show()
        })
        recyclerView.adapter = adapter

        findViewById<android.widget.EditText>(R.id.etSearch).addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterSongs(s.toString(), adapter)
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })
    }

    private fun filterSongs(query: String, adapter: SongAdapter) {
        filteredSongList.clear()
        filteredSongPaths.clear()
        for (i in songList.indices) {
            if (songList[i].lowercase().contains(query.lowercase())) {
                filteredSongList.add(songList[i])
                filteredSongPaths.add(songPaths[i])
            }
        }
        adapter.notifyDataSetChanged()
    }

    private fun setupLibraryView() {
        val rvLiked = findViewById<RecyclerView>(R.id.rvLikedSongs)
        rvLiked.layoutManager = LinearLayoutManager(this)
        
        // Filter songs that are liked
        val likedSongs = ArrayList<String>()
        val likedPaths = ArrayList<String>()
        for (i in songPaths.indices) {
            if (likedSongPaths.contains(songPaths[i])) {
                likedSongs.add(songList[i])
                likedPaths.add(songPaths[i])
            }
        }
        
        val likedAdapter = SongAdapter(likedSongs, { position ->
            val intent = Intent(this, PlayerActivity::class.java)
            intent.putExtra("songPaths", likedPaths)
            intent.putExtra("songNames", likedSongs)
            intent.putExtra("position", position)
            startActivity(intent)
        }, { position, isLiked ->
            if (!isLiked) {
                likedSongPaths.remove(likedPaths[position])
                setupLibraryView()
            }
        })
        rvLiked.adapter = likedAdapter

        val rvPlaylists = findViewById<RecyclerView>(R.id.rvPlaylists)
        rvPlaylists.layoutManager = LinearLayoutManager(this)
        val playlistAdapter = PlaylistAdapter(playlists) { playlist ->
            Toast.makeText(this, "Opening ${playlist.name}", Toast.LENGTH_SHORT).show()
        }
        rvPlaylists.adapter = playlistAdapter
    }
}
