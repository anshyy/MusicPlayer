package com.example.musicplayer

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnLogin: MaterialButton
    private lateinit var btnBack: ImageButton
    private lateinit var tvForgotPassword: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize views
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        btnBack = findViewById(R.id.btnBack)
        tvForgotPassword = findViewById(R.id.tvForgotPassword)

        // Back button functionality
        btnBack.setOnClickListener {
            finish() // Go back to previous activity
        }

        // Login button functionality
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            // Validate inputs
            when {
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
                    // All validations passed - proceed with login
                    performLogin(email, password)
                }
            }
        }

        // Forgot password functionality
        tvForgotPassword.setOnClickListener {
            // For demo purposes, show a message
            Toast.makeText(this, "Password reset feature coming soon! 📧", Toast.LENGTH_SHORT).show()
        }

        // Register link functionality
        findViewById<TextView>(R.id.tvRegisterLink).setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish() // Close login activity
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return email.matches(emailPattern.toRegex())
    }

    private fun performLogin(email: String, password: String) {
        // For this demo app, we'll simulate login success
        // In a real app, you would make API calls to your backend

        // Show loading state
        btnLogin.isEnabled = false
        btnLogin.text = "Signing In..."

        // Simulate network delay (remove this in production)
        btnLogin.postDelayed({
            // Login "successful"
            val username = email.substringBefore("@") // Extract username from email
            Toast.makeText(this, "Welcome back, $username! 🎵", Toast.LENGTH_LONG).show()

            // Navigate to main app
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }, 1500) // 1.5 second delay for demo
    }
}
