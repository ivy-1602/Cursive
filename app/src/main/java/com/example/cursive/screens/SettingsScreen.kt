package com.example.cursive.screens

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cursive.databinding.ActivitySettingsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class SettingsScreen : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        setupListeners()
        loadPinStatus()
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        // Change PIN
        binding.btnChangePin.setOnClickListener {
            showChangePinDialog()
        }

        // About Cursive
        binding.btnAbout.setOnClickListener {
            startActivity(Intent(this, AboutScreen::class.java))
        }
    }

    private fun loadPinStatus() {
        val sharedPrefs = getSharedPreferences("CursivePrefs", Context.MODE_PRIVATE)
        val pinEnabled = sharedPrefs.getBoolean("pin_enabled", false)

        // Set the switch state WITHOUT triggering the listener
        binding.switchPinLock.setOnCheckedChangeListener(null)
        binding.switchPinLock.isChecked = pinEnabled

        // NOW set the listener (after loading the state)
        binding.switchPinLock.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                showSetPinDialog()
            } else {
                disablePin()
            }
        }
    }

    private fun showSetPinDialog() {
        val input = EditText(this)
        input.hint = "Enter 4-digit PIN"
        input.inputType = android.text.InputType.TYPE_CLASS_NUMBER or
                android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD
        input.maxEms = 4

        val container = LinearLayout(this)
        container.setPadding(50, 20, 50, 20)
        container.addView(input)

        AlertDialog.Builder(this)
            .setTitle("Set PIN")
            .setMessage("Create a 4-digit PIN to protect your journal")
            .setView(container)
            .setPositiveButton("Set PIN") { _, _ ->
                val pin = input.text.toString()
                if (pin.length == 4 && pin.all { it.isDigit() }) {
                    savePin(pin)
                    Toast.makeText(this, "PIN enabled", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "PIN must be 4 digits", Toast.LENGTH_SHORT).show()
                    binding.switchPinLock.isChecked = false
                }
            }
            .setNegativeButton("Cancel") { _, _ ->
                binding.switchPinLock.isChecked = false
            }
            .setCancelable(false)
            .show()
    }

    private fun showChangePinDialog() {
        val sharedPrefs = getSharedPreferences("CursivePrefs", Context.MODE_PRIVATE)
        val pinEnabled = sharedPrefs.getBoolean("pin_enabled", false)

        if (!pinEnabled) {
            Toast.makeText(this, "PIN is not enabled", Toast.LENGTH_SHORT).show()
            return
        }

        val input = EditText(this)
        input.hint = "Enter new 4-digit PIN"
        input.inputType = android.text.InputType.TYPE_CLASS_NUMBER or
                android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD
        input.maxEms = 4

        val container = LinearLayout(this)
        container.setPadding(50, 20, 50, 20)
        container.addView(input)

        AlertDialog.Builder(this)
            .setTitle("Change PIN")
            .setMessage("Enter your new 4-digit PIN")
            .setView(container)
            .setPositiveButton("Update") { _, _ ->
                val pin = input.text.toString()
                if (pin.length == 4 && pin.all { it.isDigit() }) {
                    savePin(pin)
                    Toast.makeText(this, "PIN updated", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "PIN must be 4 digits", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun savePin(pin: String) {
        val sharedPrefs = getSharedPreferences("CursivePrefs", Context.MODE_PRIVATE)
        sharedPrefs.edit().apply {
            putString("user_pin", pin)
            putBoolean("pin_enabled", true)
            apply()
        }
    }

    private fun disablePin() {
        AlertDialog.Builder(this)
            .setTitle("Disable PIN Lock")
            .setMessage("Are you sure you want to disable PIN protection?")
            .setPositiveButton("Disable") { _, _ ->
                val sharedPrefs = getSharedPreferences("CursivePrefs", Context.MODE_PRIVATE)
                sharedPrefs.edit().apply {
                    putBoolean("pin_enabled", false)
                    remove("user_pin")
                    apply()
                }
                Toast.makeText(this, "PIN disabled", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel") { _, _ ->
                binding.switchPinLock.isChecked = true
            }
            .show()
    }
}