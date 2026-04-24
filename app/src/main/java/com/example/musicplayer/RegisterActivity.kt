package com.example.musicplayer

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class RegisterActivity : AppCompatActivity() {

    private lateinit var etUsername: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnRegister: MaterialButton
    private lateinit var btnBack: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize views
        etUsername = findViewById(R.id.etUsername)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnRegister = findViewById(R.id.btnRegister)
        btnBack = findViewById(R.id.btnBack)

        // Back button functionality
        btnBack.setOnClickListener {
            finish() // Go back to previous activity
        }

        // Register button functionality
        btnRegister.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            // Validate inputs
            when {
                username.isEmpty() -> {
                    etUsername.error = "Username is required"
                    etUsername.requestFocus()
                }
                username.length < 3 -> {
                    etUsername.error = "Username must be at least 3 characters"
                    etUsername.requestFocus()
                }
                email.isEmpty() -> {
                    etEmail.error = "Email is required"
                    etEmail.requestFocus()
                }
                !isValidEmail(email) -> {
                    etEmail.error = "Please enter a valid email address"
                    etEmail.requestFocus()
                }
                password.isEmpty() -> {
                    etPassword.error = "Password is required"
                    etPassword.requestFocus()
                }
                password.length < 6 -> {
                    etPassword.error = "Password must be at least 6 characters"
                    etPassword.requestFocus()
                }
                else -> {
                    // All validations passed - proceed with registration
                    performRegistration(username, email, password)
                }
            }
        }

        // Login link functionality
        findViewById<android.widget.TextView>(R.id.tvLoginLink).setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Close register activity
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return email.matches(emailPattern.toRegex())
    }

    private fun performRegistration(username: String, email: String, password: String) {
        // For this demo app, we'll simulate registration success
        // In a real app, you would make API calls to your backend

        // Show loading state
        btnRegister.isEnabled = false
        btnRegister.text = "Creating Account..."

        // Simulate network delay (remove this in production)
        btnRegister.postDelayed({
            // Registration "successful"
            Toast.makeText(this, "Welcome to Music Player, $username! 🎵", Toast.LENGTH_LONG).show()

            // Save login state
            getSharedPreferences("MusicPlayerPrefs", MODE_PRIVATE).edit()
                .putBoolean("isLoggedIn", true)
                .apply()

            // Navigate to main app
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }, 1500) // 1.5 second delay for demo
    }
}
