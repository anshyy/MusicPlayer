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
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
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
    private lateinit var authContainer: View
    private lateinit var profileContainer: View

    private var isLoggedIn = false
    private lateinit var sharedPreferences: android.content.SharedPreferences

    private val songList = ArrayList<Song>()
    private val searchResultsList = ArrayList<Song>()
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

    private val songChangedListener: (Int) -> Unit = { _ ->
        tvMiniSongName.text = MusicPlayerManager.getCurrentSongName()
        val uri = MusicPlayerManager.getCurrentSongArtUri()
        if (uri != null && !isFinishing && !isDestroyed) {
            Glide.with(this).load(uri).into(findViewById<ImageView>(R.id.ivMiniArt))
        }
        miniPlayer.visibility = View.VISIBLE
    }

    private val playbackStatusListener: (Boolean) -> Unit = { isPlaying ->
        btnMiniPlay.setImageResource(if (isPlaying) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play)
    }

    private val likedSongsChangeListener: () -> Unit = {
        saveLikedSongs()
        setupLibraryView()
        // Refresh adapters if needed
        recyclerView.adapter?.notifyDataSetChanged()
        rvSearchResults.adapter?.notifyDataSetChanged()
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
        authContainer = findViewById(R.id.authContainer)
        profileContainer = findViewById(R.id.profileContainer)

        sharedPreferences = getSharedPreferences("MusicPlayerPrefs", MODE_PRIVATE)
        isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        val savedLikes = sharedPreferences.getStringSet("likedSongs", emptySet())
        if (savedLikes != null) {
            MusicPlayerManager.likedSongPaths.clear()
            MusicPlayerManager.likedSongPaths.addAll(savedLikes)
        }
        updateAuthUI()

        findViewById<ImageButton>(R.id.btnMenu).setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        findViewById<TextView>(R.id.btnLogin).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        findViewById<ImageView>(R.id.btnProfile).setOnClickListener {
            showProfileMenu(it)
        }

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> bottomNav.selectedItemId = R.id.nav_home
                R.id.nav_friends -> startActivity(Intent(this, FriendsActivity::class.java))
                R.id.nav_settings -> startActivity(Intent(this, SettingsActivity::class.java))
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        miniPlayer.setOnClickListener {
            val intent = Intent(this, PlayerActivity::class.java)
            startActivity(intent)
        }

        btnMiniPlay.setOnClickListener {
            MusicPlayerManager.togglePlayPause()
        }

        MusicPlayerManager.addOnPlaybackStatusChangedListener(playbackStatusListener)
        MusicPlayerManager.addOnSongChangedListener(songChangedListener)
        MusicPlayerManager.addOnLikedSongsChangedListener(likedSongsChangeListener)

        bottomNav.setOnItemSelectedListener { item ->
            updateTab(item.itemId)
            true
        }

        // Set initial state
        updateTab(R.id.nav_home)

        // Setup search tab listener
        setupSearchListener()

        // Load background image
        Glide.with(this)
            .load("https://w0.peakpx.com/wallpaper/559/385/HD-wallpaper-monkey-d-luffy-one-piece-anime.jpg")
            .into(findViewById<ImageView>(R.id.appBackground))

        if (hasPermission()) {
            loadSongs()
        } else {
            requestPermission()
        }
        mockArtistSongs()
    }

    private fun mockArtistSongs() {
        val artistImage = android.net.Uri.parse("https://i.scdn.co/image/ab6761610000e5eb1d206f47055728a55639e763") // Juice WRLD Image
        val juiceSongs = listOf(
            "Lucid Dreams", "All Girls Are The Same", "Robbery", "Wishing Well", "Come & Go",
            "Lean Wit Me", "Legends", "Righteous", "Empty", "Black & White",
            "Fast", "Maze", "Flaws and Sins", "Hear Me Calling", "Ring Ring",
            "Fine China", "Wasted", "Armed and Dangerous", "Rich and Blind", "Candles",
            "Scared of Love", "Used To", "Hurt Me", "I'll Be Fine", "734",
            "Already Dead", "Cigarettes", "Go Hard", "Feline", "Rockstar In His Prime",
            "Burn", "You Wouldn't Understand", "Sometimes", "Not Enough", "Doom",
            "Relocate", "Reminds Me Of You", "Smile", "Real Shit", "Bad Boy",
            "Life's A Mess", "Tell Me U Luv Me", "Conversations", "Titanic", "Bad Energy",
            "Blood On My Jeans", "Hate The Other Side", "I Want It", "Fighting Demons", "Up Up and Away",
            "Stay High", "Can't Die", "Man Of The Year", "Screw Juice", "The Bees Knees",
            "HeMotions", "Feeling", "Syphilis", "Who Shot Cupid?", "10 Feet",
            "Won't Let Go", "She's The One", "Rider", "Make Believe", "No Issue",
            "Astronauts", "7 AM Freestyle", "Jet Lag", "Hard Work Pays Off", "Realer N Realer",
            "No Bystanders", "Can't Leave Without It", "MoshPit", "Demons and Angels", "Yell-Oh",
            "Suicidal (Remix)", "Flex", "PTSD", "Hate Me", "Ransom (Remix)",
            "Nuketown", "On God", "1400 / 999 Freestyle", "Matt Hardy 999", "Blastoff",
            "Buck 50", "Paranoid", "Autograph (On My Line)", "My Fault", "Golden X82",
            "Moonlight", "Let Me Know (I Wonder Why Freestyle)", "Eye of the Tiger", "Run", "Girl Of My Dreams",
            "You Might Need It", "Inner Peace", "High Tide", "Remind Me of the Summer", "Carry It"
        )

        juiceSongs.forEachIndexed { index, title ->
            songList.add(Song(title, "Juice WRLD", "mock_path_$index", index.toLong(), artistImage))
        }
        recyclerView.adapter?.notifyDataSetChanged()
    }

    private fun updateTab(itemId: Int) {
        homeView.visibility = if (itemId == R.id.nav_home) View.VISIBLE else View.GONE
        libraryView.visibility = if (itemId == R.id.nav_library) View.VISIBLE else View.GONE
        searchView.visibility = if (itemId == R.id.nav_search) View.VISIBLE else View.GONE

        when (itemId) {
            R.id.nav_home -> {
                tvTitle.text = "Listen Now"
            }
            R.id.nav_library -> {
                tvTitle.text = "Library"
                setupLibraryView()
            }
            R.id.nav_search -> {
                tvTitle.text = "Search"
                etSearchTabInput.requestFocus()
            }
        }
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
        
        if (rvSearchResults.adapter == null) {
            val adapter = SongAdapter(searchResultsList, MusicPlayerManager.likedSongPaths, { position ->
                val song = searchResultsList[position]
                MusicPlayerManager.currentSongList = searchResultsList.map { it.title }
                MusicPlayerManager.currentSongPaths = searchResultsList.map { it.path }
                MusicPlayerManager.currentSongArtUris = searchResultsList.map { it.albumArtUri }
                MusicPlayerManager.currentArtists = searchResultsList.map { it.artist }
                MusicPlayerManager.playSong(this, position)
                addToRecentlyPlayed(song)
            }, { position, _ ->
                val path = searchResultsList[position].path
                MusicPlayerManager.toggleLike(path)
            })
            rvSearchResults.adapter = adapter
        } else {
            rvSearchResults.adapter?.notifyDataSetChanged()
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

        val adapter = SongAdapter(songList, MusicPlayerManager.likedSongPaths, { position ->
            MusicPlayerManager.currentSongList = songList.map { it.title }
            MusicPlayerManager.currentSongPaths = songList.map { it.path }
            MusicPlayerManager.currentSongArtUris = songList.map { it.albumArtUri }
            MusicPlayerManager.currentArtists = songList.map { it.artist }
            MusicPlayerManager.playSong(this, position)
            addToRecentlyPlayed(songList[position])
        }, { position, _ ->
            val path = songList[position].path
            MusicPlayerManager.toggleLike(path)
        })
        recyclerView.adapter = adapter

        // Setup homepage with daily mixes and recently played
        setupHomepage()
        setupLibraryView()
    }

    private fun setupLibraryView() {
        val rvLiked = findViewById<RecyclerView>(R.id.rvLikedSongs)
        rvLiked.layoutManager = LinearLayoutManager(this)
        
        val likedSongs = songList.filter { MusicPlayerManager.likedSongPaths.contains(it.path) }
        
        if (likedSongs.isEmpty()) {
            // No songs liked yet
        }

        val likedAdapter = SongAdapter(likedSongs, MusicPlayerManager.likedSongPaths, { position ->
            val song = likedSongs[position]
            MusicPlayerManager.currentSongList = likedSongs.map { it.title }
            MusicPlayerManager.currentSongPaths = likedSongs.map { it.path }
            MusicPlayerManager.currentSongArtUris = likedSongs.map { it.albumArtUri }
            MusicPlayerManager.currentArtists = likedSongs.map { it.artist }
            MusicPlayerManager.playSong(this, position)
            addToRecentlyPlayed(song)
        }, { position, _ ->
            val path = likedSongs[position].path
            MusicPlayerManager.toggleLike(path)
        })
        rvLiked.adapter = likedAdapter

        val rvPlaylists = findViewById<RecyclerView>(R.id.rvPlaylists)
        rvPlaylists.layoutManager = LinearLayoutManager(this)
        
        // Add some mock playlists if empty for demonstration
        if (playlists.isEmpty() && songList.isNotEmpty()) {
            playlists.add(Playlist("My Favorites", null, ArrayList(songList.take(5).map { it.path })))
            playlists.add(Playlist("Chill Mix", null, ArrayList(songList.takeLast(5).map { it.path })))
        }

        val playlistAdapter = PlaylistAdapter(playlists, { playlist ->
            val context = this
            val intent = Intent(context, ListDetailActivity::class.java).apply {
                putExtra("list_title", playlist.name)
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
                mixSongs.add(songList[idx].title)
                mixPaths.add(songList[idx].path)
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

    private fun saveLikedSongs() {
        sharedPreferences.edit().putStringSet("likedSongs", MusicPlayerManager.likedSongPaths).apply()
    }

    private fun updateAuthUI() {
        if (isLoggedIn) {
            authContainer.visibility = View.GONE
            profileContainer.visibility = View.VISIBLE
        } else {
            authContainer.visibility = View.VISIBLE
            profileContainer.visibility = View.GONE
        }
    }

    private fun saveLoginState(loggedIn: Boolean) {
        isLoggedIn = loggedIn
        sharedPreferences.edit().putBoolean("isLoggedIn", loggedIn).apply()
        updateAuthUI()
    }

    private fun showProfileMenu(view: View) {
        val popup = android.widget.PopupMenu(this, view)
        popup.menu.add("View Profile")
        popup.menu.add("Logout")
        
        popup.setOnMenuItemClickListener { item ->
            when (item.title) {
                "View Profile" -> {
                    Toast.makeText(this, "Profile feature coming soon!", Toast.LENGTH_SHORT).show()
                }
                "Logout" -> {
                    saveLoginState(false)
                    Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
                }
            }
            true
        }
        popup.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        MusicPlayerManager.removeOnSongChangedListener(songChangedListener)
        MusicPlayerManager.removeOnPlaybackStatusChangedListener(playbackStatusListener)
        MusicPlayerManager.removeOnLikedSongsChangedListener(likedSongsChangeListener)
    }
}
