package com.example.musicplayer

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat

class SettingsActivity : AppCompatActivity() {

    private lateinit var switchShuffle: SwitchCompat
    private lateinit var switchRepeat: SwitchCompat
    private lateinit var switchGapless: SwitchCompat
    private lateinit var switchNormalization: SwitchCompat
    private lateinit var tvCurrentQuality: TextView
    private lateinit var tvSleepTimerStatus: TextView
    private lateinit var tvCrossfadeStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Initialize views
        switchShuffle = findViewById(R.id.switchShuffle)
        switchRepeat = findViewById(R.id.switchRepeat)
        switchGapless = findViewById(R.id.switchGapless)
        switchNormalization = findViewById(R.id.switchNormalization)
        tvCurrentQuality = findViewById(R.id.tvCurrentQuality)
        tvSleepTimerStatus = findViewById(R.id.tvSleepTimerStatus)
        tvCrossfadeStatus = findViewById(R.id.tvCrossfadeStatus)

        // Set initial states from MusicPlayerManager
        switchShuffle.isChecked = MusicPlayerManager.isShuffle
        switchRepeat.isChecked = MusicPlayerManager.isRepeat
        switchGapless.isChecked = MusicPlayerManager.isGapless
        switchNormalization.isChecked = MusicPlayerManager.isNormalization
        
        tvCrossfadeStatus.text = if (MusicPlayerManager.crossfadeDuration > 0) "${MusicPlayerManager.crossfadeDuration} seconds" else "Off"
        
        // Listeners for switches
        switchShuffle.setOnCheckedChangeListener { _, isChecked ->
            MusicPlayerManager.isShuffle = isChecked
            Toast.makeText(this, if (isChecked) "Shuffle on" else "Shuffle off", Toast.LENGTH_SHORT).show()
        }

        switchRepeat.setOnCheckedChangeListener { _, isChecked ->
            MusicPlayerManager.isRepeat = isChecked
            Toast.makeText(this, if (isChecked) "Repeat on" else "Repeat off", Toast.LENGTH_SHORT).show()
        }

        switchGapless.setOnCheckedChangeListener { _, isChecked ->
            MusicPlayerManager.isGapless = isChecked
            Toast.makeText(this, if (isChecked) "Gapless playback on" else "Gapless playback off", Toast.LENGTH_SHORT).show()
        }

        switchNormalization.setOnCheckedChangeListener { _, isChecked ->
            MusicPlayerManager.updateNormalization(isChecked)
            Toast.makeText(this, if (isChecked) "Audio normalization on" else "Audio normalization off", Toast.LENGTH_SHORT).show()
        }

        // Click listeners for options
        findViewById<android.view.View>(R.id.btnAudioQuality).setOnClickListener {
            showAudioQualityDialog()
        }

        findViewById<android.view.View>(R.id.btnCrossfade).setOnClickListener {
            showCrossfadeDialog()
        }

        findViewById<android.view.View>(R.id.btnEqualizer).setOnClickListener {
            Toast.makeText(this, "Equalizer coming soon!", Toast.LENGTH_SHORT).show()
        }

        findViewById<android.view.View>(R.id.btnSleepTimer).setOnClickListener {
            showSleepTimerDialog()
        }
    }

    private fun showAudioQualityDialog() {
        val qualities = arrayOf("Automatic", "Normal", "High", "Very High")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Audio Quality")
        builder.setItems(qualities) { _, which ->
            val selected = qualities[which]
            tvCurrentQuality.text = selected
        }
        builder.show()
    }

    private fun showCrossfadeDialog() {
        val options = arrayOf("Off", "2 seconds", "4 seconds", "8 seconds", "12 seconds")
        val values = intArrayOf(0, 2, 4, 8, 12)
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Crossfade")
        builder.setItems(options) { _, which ->
            MusicPlayerManager.crossfadeDuration = values[which]
            tvCrossfadeStatus.text = options[which]
            Toast.makeText(this, "Crossfade set to ${options[which]}", Toast.LENGTH_SHORT).show()
        }
        builder.show()
    }

    private fun showSleepTimerDialog() {
        val options = arrayOf("Off", "5 minutes", "15 minutes", "30 minutes", "1 hour")
        val values = intArrayOf(0, 5, 15, 30, 60)
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Sleep Timer")
        builder.setItems(options) { _, which ->
            val selected = options[which]
            tvSleepTimerStatus.text = selected
            
            MusicPlayerManager.setSleepTimer(values[which]) {
                Toast.makeText(applicationContext, "Sleep timer finished. Music paused.", Toast.LENGTH_LONG).show()
            }

            if (selected == "Off") {
                Toast.makeText(this, "Sleep timer disabled", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Sleep timer set for $selected", Toast.LENGTH_SHORT).show()
            }
        }
        builder.show()
    }
}