package com.example.cursive.screens

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.cursive.databinding.ActivityPinBinding
import com.google.firebase.auth.FirebaseAuth

class PinScreen : AppCompatActivity() {

    private lateinit var binding: ActivityPinBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // Check if user is logged in
        if (auth.currentUser == null) {
            navigateToLogin()
            return
        }

        checkPinStatus()
        setupPinInput()
        setupBackPressHandler()
    }

    private fun setupBackPressHandler() {
        // Modern way to handle back press
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Disable back button - user must enter PIN or logout
                // Do nothing here to prevent going back
            }
        })
    }

    private fun checkPinStatus() {
        val sharedPrefs = getSharedPreferences("CursivePrefs", Context.MODE_PRIVATE)
        val pinEnabled = sharedPrefs.getBoolean("pin_enabled", false)

        if (!pinEnabled) {
            // PIN is not enabled, go directly to home
            navigateToHome()
            return
        }

        // PIN is enabled, show unlock screen
        binding.tvPinMessage.text = "Enter your 4-digit PIN"
        binding.btnSubmit.text = "Unlock"
        binding.tvSkip.text = "Forgot PIN?"
    }

    private fun setupPinInput() {
        // Auto-enable submit button when 4 digits are entered
        binding.etPin.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                binding.btnSubmit.isEnabled = s?.length == 4
            }
        })

        // Submit button
        binding.btnSubmit.setOnClickListener {
            val enteredPin = binding.etPin.text.toString()
            if (enteredPin.length == 4) {
                verifyPin(enteredPin)
            } else {
                Toast.makeText(this, "Please enter 4 digits", Toast.LENGTH_SHORT).show()
            }
        }

        // Forgot PIN
        binding.tvSkip.setOnClickListener {
            showForgotPinDialog()
        }

        binding.btnSubmit.isEnabled = false
    }

    private fun verifyPin(enteredPin: String) {
        val sharedPrefs = getSharedPreferences("CursivePrefs", Context.MODE_PRIVATE)
        val storedPin = sharedPrefs.getString("user_pin", null)

        if (enteredPin == storedPin) {
            navigateToHome()
        } else {
            Toast.makeText(this, "Incorrect PIN", Toast.LENGTH_SHORT).show()
            binding.etPin.text?.clear()
        }
    }

    private fun showForgotPinDialog() {
        AlertDialog.Builder(this)
            .setTitle("Forgot PIN?")
            .setMessage("To reset your PIN, you need to disable it in Settings after logging in, or logout and login again.")
            .setPositiveButton("Logout") { _, _ ->
                auth.signOut()
                navigateToLogin()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun navigateToHome() {
        startActivity(Intent(this, HomeScreen::class.java))
        finish()
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginScreen::class.java))
        finish()
    }
}