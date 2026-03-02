package com.example.cursive.screens

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.cursive.R
import com.google.firebase.auth.FirebaseAuth

class SplashScreen : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        auth = FirebaseAuth.getInstance()

        // Show splash for 2 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            checkUserAndNavigate()
        }, 2000) // 2 seconds
    }

    private fun checkUserAndNavigate() {
        val currentUser = auth.currentUser

        if (currentUser != null) {
            // User is logged in, go to PIN screen
            startActivity(Intent(this, PinScreen::class.java))
        } else {
            // User not logged in, go to login
            startActivity(Intent(this, LoginScreen::class.java))
        }
        finish()
    }
}