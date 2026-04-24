package com.example.musicplayer

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

class FriendsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends)

        findViewById<Button>(R.id.btnScanFriend).setOnClickListener {
            Toast.makeText(this, "Scanning feature coming soon!", Toast.LENGTH_SHORT).show()
        }
    }
}