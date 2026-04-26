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
                R.id.nav_library -> switchToLibraryTab()
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        bottomNav.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> switchToHomeTab()
                R.id.nav_search -> switchToSearchTab()
                R.id.nav_library -> switchToLibraryTab()
            }
            true
        }

        checkPermission()
        setupMiniPlayer()
        setupSearch()
    }

    private fun setupSearch() {
        etSearchTabInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().lowercase()
                searchResultsList.clear()
                if (query.isNotEmpty()) {
                    searchResultsList.addAll(songList.filter { 
                        it.title.lowercase().contains(query) || it.artist.lowercase().contains(query)
                    })
                }
                updateSearchResults()
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })
    }

    private fun updateSearchResults() {
        val adapter = SongAdapter(searchResultsList, likedSongPaths, { position ->
            val song = searchResultsList[position]
            val idx = songList.indexOfFirst { it.path == song.path }
            if (idx != -1) {
                MusicPlayerManager.currentSongList = songList.map { it.title }
                MusicPlayerManager.currentSongPaths = songList.map { it.path }
                MusicPlayerManager.currentSongArtUris = songList.map { it.albumArtUri }
                MusicPlayerManager.currentArtists = songList.map { it.artist }
                MusicPlayerManager.playSong(this, idx)
                addToRecentlyPlayed(song)
                startActivity(Intent(this, PlayerActivity::class.java))
            }
        }, { position, isLiked ->
            val path = searchResultsList[position].path
            if (isLiked) likedSongPaths.add(path) else likedSongPaths.remove(path)
            setupLibraryView()
        })
        rvSearchResults.adapter = adapter
    }

    private fun setupMiniPlayer() {
        miniPlayer.setOnClickListener {
            if (MusicPlayerManager.currentPosition != -1) {
                startActivity(Intent(this, PlayerActivity::class.java))
            }
        }
        btnMiniPlay.setOnClickListener {
            // Toggle play/pause - logic in MusicPlayerManager could be expanded
        }
        MusicPlayerManager.onSongChanged = { position ->
            tvMiniSongName.text = MusicPlayerManager.currentSongList[position]
            miniPlayer.visibility = View.VISIBLE
        }
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_MEDIA_AUDIO), permissionCode)
            } else {
                loadSongs()
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), permissionCode)
            } else {
                loadSongs()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == permissionCode && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadSongs()
        } else {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
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
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        
        contentResolver.query(uri, projection, selection, null, null)?.use {
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
                // Map the paths back to Song objects
                val songs = ArrayList<Song>()
                for (path in playlist.songPaths) {
                    val song = songList.find { it.path == path }
                    if (song != null) {
                        songs.add(song)
                    }
                }
                putParcelableArrayListExtra("songs", songs)
            }
            startActivity(intent)
        })
        rvPlaylists.adapter = playlistAdapter
        
        findViewById<View>(R.id.btnAddPlaylist).setOnClickListener {
            val intent = Intent(this, PlaylistEditActivity::class.java)
            val allSongPaths = ArrayList(songList.map { it.path })
            val allSongTitles = ArrayList(songList.map { it.title })
            intent.putStringArrayListExtra("all_song_paths", allSongPaths)
            intent.putStringArrayListExtra("all_song_titles", allSongTitles)
            createPlaylistLauncher.launch(intent)
        }

        findViewById<View>(R.id.catHipHop).setOnClickListener {
            openCategoryPlaylist("Hip Hop")
        }
        findViewById<View>(R.id.catPop).setOnClickListener {
            openCategoryPlaylist("Pop")
        }
        findViewById<View>(R.id.catBlues).setOnClickListener {
            openCategoryPlaylist("Blues")
        }
    }

    private fun openCategoryPlaylist(category: String) {
        val filteredSongs = ArrayList<Song>()
        
        // Simulating category filtering - in a real app you'd have genre metadata
        // For now, we'll just pick some random songs if the list is large enough
        if (songList.isNotEmpty()) {
            for (i in 0 until minOf(10, songList.size)) {
                filteredSongs.add(songList[i])
            }
        }

        val intent = Intent(this, ListDetailActivity::class.java).apply {
            putExtra("list_title", category)
            putParcelableArrayListExtra("songs", filteredSongs)
        }
        startActivity(intent)
    }

    private fun switchToHomeTab() {
        homeView.visibility = View.VISIBLE
        libraryView.visibility = View.GONE
        searchView.visibility = View.GONE
        tvTitle.text = "Home"
    }

    private fun switchToSearchTab() {
        homeView.visibility = View.GONE
        libraryView.visibility = View.GONE
        searchView.visibility = View.VISIBLE
        tvTitle.text = "Search"
    }

    private fun switchToLibraryTab() {
        homeView.visibility = View.GONE
        libraryView.visibility = View.VISIBLE
        searchView.visibility = View.GONE
        tvTitle.text = "Your Library"
        setupLibraryView()
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
            val mixSongs = ArrayList<Song>()

            for (j in 0 until songsPerMix) {
                val idx = (startIndex + j) % songList.size
                mixSongs.add(songList[idx])
            }

            dailyMixes.add(DailyMix(
                id = "mix_${i}",
                title = mixTitles[i],
                description = mixDescriptions[i],
                imageUri = if (mixSongs.isNotEmpty()) mixSongs[0].albumArtUri else null,
                color = colors[i % colors.size],
                songs = mixSongs
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
            MusicPlayerManager.currentSongList = mix.songs.map { it.title }
            MusicPlayerManager.currentSongPaths = mix.songs.map { it.path }
            MusicPlayerManager.currentSongArtUris = mix.songs.map { it.albumArtUri }
            MusicPlayerManager.currentArtists = mix.songs.map { it.artist }
            
            if (mix.songs.isNotEmpty()) {
                MusicPlayerManager.playSong(this, 0)
                addToRecentlyPlayed(mix.songs[0])
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
