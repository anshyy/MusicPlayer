package com.example.musicplayer

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class PlaylistEditActivity : AppCompatActivity() {

    private lateinit var ivPlaylistThumb: ImageView
    private lateinit var etPlaylistName: TextInputEditText
    private lateinit var rvSongsForPlaylist: RecyclerView
    private var selectedImageUri: Uri? = null
    private val selectedSongPaths = ArrayList<String>()
    private val allSongs = ArrayList<String>()
    private val allSongPaths = ArrayList<String>()

    private val selectImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            selectedImageUri = result.data?.data
            ivPlaylistThumb.setImageURI(selectedImageUri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playlist_edit)

        ivPlaylistThumb = findViewById(R.id.ivPlaylistThumb)
        etPlaylistName = findViewById(R.id.etPlaylistName)
        rvSongsForPlaylist = findViewById(R.id.rvSongsForPlaylist)
        val btnSave = findViewById<MaterialButton>(R.id.btnSavePlaylist)
        val cvPlaylistImage = findViewById<androidx.cardview.widget.CardView>(R.id.cvPlaylistImage)

        rvSongsForPlaylist.layoutManager = LinearLayoutManager(this)

        // Load songs from device
        loadSongsFromDevice()

        cvPlaylistImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            selectImageLauncher.launch(intent)
        }

        btnSave.setOnClickListener {
            val name = etPlaylistName.text.toString()
            if (name.isNotEmpty()) {
                val resultIntent = Intent()
                resultIntent.putExtra("playlist_name", name)
                resultIntent.putExtra("playlist_image", selectedImageUri)
                resultIntent.putStringArrayListExtra("playlist_songs", selectedSongPaths)
                setResult(RESULT_OK, resultIntent)
                Toast.makeText(this, "Playlist '$name' created!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Please enter a playlist name", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadSongsFromDevice() {
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATA
        )
        val cursor = contentResolver.query(uri, projection, null, null, null)

        cursor?.use {
            while (it.moveToNext()) {
                val title = it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE))
                val path = it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))
                allSongs.add(title)
                allSongPaths.add(path)
            }
        }

        if (allSongs.isNotEmpty()) {
            val adapter = PlaylistSongAdapter(allSongs, selectedSongPaths) { selectedPaths: ArrayList<String> ->
                this.selectedSongPaths.clear()
                this.selectedSongPaths.addAll(selectedPaths)
            }
            rvSongsForPlaylist.adapter = adapter
        }
    }
}
