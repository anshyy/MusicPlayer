package com.example.musicplayer

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var recyclerView: RecyclerView
    private lateinit var homeView: View
    private lateinit var libraryView: View
    private lateinit var searchView: View
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var miniPlayer: View
    private lateinit var tvMiniSongName: TextView
    private lateinit var btnMiniPlay: ImageButton
    private lateinit var rvDailyMixes: RecyclerView
    private lateinit var rvRecentlyPlayed: RecyclerView
    private lateinit var rvSearchResults: RecyclerView
    private lateinit var etSearchTabInput: EditText
    private lateinit var tvTitle: TextView

    private val songList = ArrayList<Song>()
    private val searchResultsList = ArrayList<Song>()
    private val likedSongPaths = HashSet<String>()
    private val recentlyPlayedSongs = ArrayList<RecentlyPlayed>()
    private val dailyMixes = ArrayList<DailyMix>()
    private val playlists = ArrayList<Playlist>()
    private val permissionCode = 1

    private val createPlaylistLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val name = result.data?.getStringExtra("playlist_name") ?: "New Playlist"
            val imageUri = result.data?.getParcelableExtra<android.net.Uri>("playlist_image")
            val playlistSongs = result.data?.getStringArrayListExtra("playlist_songs") ?: arrayListOf()
            playlists.add(Playlist(name, imageUri, playlistSongs))
            setupLibraryView()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawerLayout)
        navView = findViewById(R.id.navView)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        
        rvDailyMixes = findViewById(R.id.rvDailyMixes)
        rvDailyMixes.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        rvRecentlyPlayed = findViewById(R.id.rvRecentlyPlayed)
        rvRecentlyPlayed.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        rvSearchResults = findViewById(R.id.rvSearchResults)
        rvSearchResults.layoutManager = LinearLayoutManager(this)

        homeView = findViewById(R.id.homeView)
        libraryView = findViewById(R.id.libraryView)
        searchView = findViewById(R.id.searchView)
        bottomNav = findViewById(R.id.bottomNavigation)
        miniPlayer = findViewById(R.id.miniPlayer)
        tvMiniSongName = findViewById(R.id.tvMiniSongName)
        btnMiniPlay = findViewById(R.id.btnMiniPlay)
        tvTitle = findViewById(R.id.tvTitle)
        etSearchTabInput = findViewById(R.id.etSearchTabInput)

        findViewById<ImageButton>(R.id.btnMenu).setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        findViewById<ImageButton>(R.id.btnSearch).setOnClickListener {
            switchToSearchTab()
        }

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> switchToHomeTab()
                R.id.nav_friends -> Toast.makeText(this, "Friends clicked", Toast.LENGTH_SHORT).show()
                R.id.nav_settings -> Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show()
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        // Setup category clicks
        setupCategoryClicks()

        miniPlayer.setOnClickListener {
            val intent = Intent(this, PlayerActivity::class.java)
            startActivity(intent)
        }

        btnMiniPlay.setOnClickListener {
            MusicPlayerManager.togglePlayPause()
        }

        MusicPlayerManager.onPlaybackStatusChanged = { isPlaying ->
            btnMiniPlay.setImageResource(if (isPlaying) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play)
        }

        MusicPlayerManager.onSongChanged = { _ ->
            tvMiniSongName.text = MusicPlayerManager.getCurrentSongName()
            miniPlayer.visibility = View.VISIBLE
        }

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    switchToHomeTab()
                    true
                }
                R.id.nav_library -> {
                    switchToLibraryTab()
                    true
                }
                R.id.nav_search -> {
                    switchToSearchTab()
                    true
                }
                else -> false
            }
        }

        findViewById<ImageButton>(R.id.btnAddPlaylist).setOnClickListener {
            val intent = Intent(this, PlaylistEditActivity::class.java)
            createPlaylistLauncher.launch(intent)
        }

        // Setup search tab listener
        setupSearchListener()

        if (hasPermission()) {
            loadSongs()
        } else {
            requestPermission()
        }
    }

    private fun setupCategoryClicks() {
        findViewById<View>(R.id.catHipHop).setOnClickListener {
            openCategoryPlaylist("Hip Hop")
        }
        findViewById<View>(R.id.catPop).setOnClickListener {
            openCategoryPlaylist("Pop Music")
        }
        findViewById<View>(R.id.catBlues).setOnClickListener {
            openCategoryPlaylist("Blues")
        }
    }

    private fun openCategoryPlaylist(category: String) {
        val filteredSongs = ArrayList<String>()
        val filteredPaths = ArrayList<String>()
        
        // Simulating category filtering - in a real app you'd have genre metadata
        // For now, we'll just pick some random songs if the list is large enough
        if (songList.isNotEmpty()) {
            for (i in 0 until minOf(10, songList.size)) {
                filteredSongs.add(songList[i].title)
                filteredPaths.add(songList[i].path)
            }
        }

        val intent = Intent(this, ListDetailActivity::class.java).apply {
            putExtra("list_title", category)
            putStringArrayListExtra("song_names", filteredSongs)
            putStringArrayListExtra("song_paths", filteredPaths)
        }
        startActivity(intent)
    }

    private fun switchToHomeTab() {
        homeView.visibility = View.VISIBLE
        libraryView.visibility = View.GONE
        searchView.visibility = View.GONE
        tvTitle.text = "Home"
        bottomNav.selectedItemId = R.id.nav_home
    }

    private fun switchToLibraryTab() {
        homeView.visibility = View.GONE
        libraryView.visibility = View.VISIBLE
        searchView.visibility = View.GONE
        tvTitle.text = "Library"
        bottomNav.selectedItemId = R.id.nav_library
        setupLibraryView()
    }

    private fun switchToSearchTab() {
        homeView.visibility = View.GONE
        libraryView.visibility = View.GONE
        searchView.visibility = View.VISIBLE
        tvTitle.text = "Search"
        bottomNav.selectedItemId = R.id.nav_search
        etSearchTabInput.requestFocus()
    }

    private fun setupSearchListener() {
        etSearchTabInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchSongs(s.toString())
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })
    }

    private fun searchSongs(query: String) {
        searchResultsList.clear()
        
        if (query.isEmpty()) {
            rvSearchResults.adapter = null
            return
        }
        
        for (song in songList) {
            if (song.title.lowercase().contains(query.lowercase()) || 
                song.artist.lowercase().contains(query.lowercase())) {
                searchResultsList.add(song)
            }
        }
        
        val adapter = SongAdapter(searchResultsList, likedSongPaths, { position ->
            val song = searchResultsList[position]
            MusicPlayerManager.currentSongList = searchResultsList.map { it.title }
            MusicPlayerManager.currentSongPaths = searchResultsList.map { it.path }
            MusicPlayerManager.currentSongArtUris = searchResultsList.map { it.albumArtUri }
            MusicPlayerManager.currentArtists = searchResultsList.map { it.artist }
            MusicPlayerManager.playSong(this, position)
            addToRecentlyPlayed(song)
            val intent = Intent(this, PlayerActivity::class.java)
            startActivity(intent)
        }, { position, isLiked ->
            val path = searchResultsList[position].path
            if (isLiked) likedSongPaths.add(path) else likedSongPaths.remove(path)
        })
        rvSearchResults.adapter = adapter
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
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID
        )
        val cursor = contentResolver.query(uri, projection, null, null, null)

        cursor?.use {
            while (it.moveToNext()) {
                val title = it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE))
                val artist = it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST))
                val path  = it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))
                val albumId = it.getLong(it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID))
                
                val artworkUri = android.content.ContentUris.withAppendedId(
                    android.net.Uri.parse("content://media/external/audio/albumart"),
                    albumId
                )
                
                songList.add(Song(title, artist, path, albumId, artworkUri))
            }
        }

        if (songList.isEmpty()) {
            Toast.makeText(this, "No songs found on device", Toast.LENGTH_SHORT).show()
            return
        }

        val adapter = SongAdapter(songList, likedSongPaths, { position ->
            MusicPlayerManager.currentSongList = songList.map { it.title }
            MusicPlayerManager.currentSongPaths = songList.map { it.path }
            MusicPlayerManager.currentSongArtUris = songList.map { it.albumArtUri }
            MusicPlayerManager.currentArtists = songList.map { it.artist }
            MusicPlayerManager.playSong(this, position)
            addToRecentlyPlayed(songList[position])

            val intent = Intent(this, PlayerActivity::class.java)
            startActivity(intent)
        }, { position, isLiked ->
            val path = songList[position].path
            if (isLiked) likedSongPaths.add(path) else likedSongPaths.remove(path)
            setupLibraryView()
        })
        recyclerView.adapter = adapter

        // Setup homepage with daily mixes and recently played
        setupHomepage()
    }

    private fun setupLibraryView() {
        val rvLiked = findViewById<RecyclerView>(R.id.rvLikedSongs)
        rvLiked.layoutManager = LinearLayoutManager(this)
        
        val likedSongs = songList.filter { likedSongPaths.contains(it.path) }
        
        val likedAdapter = SongAdapter(likedSongs, likedSongPaths, { position ->
            val song = likedSongs[position]
            MusicPlayerManager.currentSongList = likedSongs.map { it.title }
            MusicPlayerManager.currentSongPaths = likedSongs.map { it.path }
            MusicPlayerManager.currentSongArtUris = likedSongs.map { it.albumArtUri }
            MusicPlayerManager.currentArtists = likedSongs.map { it.artist }
            MusicPlayerManager.playSong(this, position)
            addToRecentlyPlayed(song)
            startActivity(Intent(this, PlayerActivity::class.java))
        }, { position, isLiked ->
            if (!isLiked) {
                likedSongPaths.remove(likedSongs[position].path)
                setupLibraryView()
            }
        })
        rvLiked.adapter = likedAdapter

        val rvPlaylists = findViewById<RecyclerView>(R.id.rvPlaylists)
        rvPlaylists.layoutManager = LinearLayoutManager(this)
        val playlistAdapter = PlaylistAdapter(playlists, { playlist ->
            val context = this
            val intent = Intent(context, ListDetailActivity::class.java).apply {
                putExtra("list_title", playlist.name)
                // Map the paths back to names
                val names = ArrayList<String>()
                val paths = ArrayList<String>()
                for (path in playlist.songPaths) {
                    val song = songList.find { it.path == path }
                    if (song != null) {
                        names.add(song.title)
                        paths.add(path)
                    }
                }
                putStringArrayListExtra("song_names", names)
                putStringArrayListExtra("song_paths", paths)
            }
            startActivity(intent)
        })
        rvPlaylists.adapter = playlistAdapter
    }

    private fun generateDailyMixes() {
        dailyMixes.clear()
        if (songList.isEmpty()) return

        val colors = listOf(
            "#6A5B95",  // Discover Weekly - Purple
            "#8B7AB8",  // New Music Daily - Light Purple
            "#9B8FCC",  // Feel Good Mix - Lighter Purple
            "#7A6BAA"   // Chill Vibes - Medium Purple
        )

        val mixTitles = listOf(
            "Discover Weekly",
            "New Music Daily",
            "Feel Good Mix",
            "Chill Vibes"
        )

        val mixDescriptions = listOf(
            "Fresh tracks picked just for you",
            "The latest tracks you'll love",
            "Songs that make you smile",
            "Relaxing tunes for your day"
        )

        for (i in mixTitles.indices) {
            val songsPerMix = minOf(15, songList.size)
            val startIndex = (i * songsPerMix) % songList.size
            val mixSongs = ArrayList<String>()
            val mixPaths = ArrayList<String>()

            for (j in 0 until songsPerMix) {
                val idx = (startIndex + j) % songList.size
                mixSongs.add(songList[idx])
                mixPaths.add(songPaths[idx])
            }

            dailyMixes.add(DailyMix(
                id = "mix_${i}",
                title = mixTitles[i],
                description = mixDescriptions[i],
                imageUri = if (mixPaths.isNotEmpty()) songList.find { it.path == mixPaths[0] }?.albumArtUri else null,
                color = colors[i % colors.size],
                songs = mixSongs,
                songPaths = mixPaths
            ))
        }
    }

    private fun setupHomepage() {
        if (songList.isEmpty()) return

        // Generate daily mixes if empty
        if (dailyMixes.isEmpty()) {
            generateDailyMixes()
        }

        // Setup Daily Mixes RecyclerView
        val dailyMixAdapter = DailyMixAdapter(dailyMixes) { mix ->
            MusicPlayerManager.currentSongList = mix.songs
            MusicPlayerManager.currentSongPaths = mix.songPaths
            MusicPlayerManager.currentSongArtUris = mix.songPaths.map { path ->
                songList.find { it.path == path }?.albumArtUri
            }
            MusicPlayerManager.currentArtists = mix.songPaths.map { path ->
                songList.find { it.path == path }?.artist ?: "Unknown"
            }
            if (mix.songPaths.isNotEmpty()) {
                MusicPlayerManager.playSong(this, 0)
                addToRecentlyPlayed(songList.find { it.path == mix.songPaths[0] } ?: Song(mix.songs[0], "Unknown", mix.songPaths[0], 0))
                startActivity(Intent(this, PlayerActivity::class.java))
            }
        }
        rvDailyMixes.adapter = dailyMixAdapter

        // Setup Recently Played RecyclerView
        val recentlyPlayedAdapter = RecentlyPlayedAdapter(recentlyPlayedSongs) { position ->
            val song = recentlyPlayedSongs[position]
            val idx = songList.indexOfFirst { it.path == song.songPath }
            if (idx != -1) {
                MusicPlayerManager.currentSongList = songList.map { it.title }
                MusicPlayerManager.currentSongPaths = songList.map { it.path }
                MusicPlayerManager.currentSongArtUris = songList.map { it.albumArtUri }
                MusicPlayerManager.currentArtists = songList.map { it.artist }
                MusicPlayerManager.playSong(this, idx)
                startActivity(Intent(this, PlayerActivity::class.java))
            }
        }
        rvRecentlyPlayed.adapter = recentlyPlayedAdapter
    }

    private fun addToRecentlyPlayed(song: Song) {
        // Remove if already exists
        recentlyPlayedSongs.removeAll { it.songPath == song.path }

        // Add to beginning
        recentlyPlayedSongs.add(0, RecentlyPlayed(song.title, song.path, System.currentTimeMillis(), song.albumArtUri))

        // Keep only last 20 songs
        while (recentlyPlayedSongs.size > 20) {
            recentlyPlayedSongs.removeAt(recentlyPlayedSongs.size - 1)
        }

        // Update adapter
        rvRecentlyPlayed.adapter?.notifyDataSetChanged()
    }
}
