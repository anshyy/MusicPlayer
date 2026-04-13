package com.example.musicplayer

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class PlaylistEditActivity : AppCompatActivity() {

    private lateinit var ivPlaylistThumb: ImageView
    private lateinit var etPlaylistName: TextInputEditText
    private var selectedImageUri: Uri? = null

    private val selectImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            selectedImageUri = result.data?.data
            ivPlaylistThumb.setImageURI(selectedImageUri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playlist_edit)

        ivPlaylistThumb = findViewById(R.id.ivPlaylistThumb)
        etPlaylistName = findViewById(R.id.etPlaylistName)
        val btnSave = findViewById<MaterialButton>(R.id.btnSavePlaylist)
        val cvPlaylistImage = findViewById<androidx.cardview.widget.CardView>(R.id.cvPlaylistImage)

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
                setResult(Activity.RESULT_OK, resultIntent)
                Toast.makeText(this, "Playlist '$name' saved!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Please enter a name", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
