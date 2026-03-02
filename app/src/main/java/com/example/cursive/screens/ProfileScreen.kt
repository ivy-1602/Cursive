package com.example.cursive.screens

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cursive.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class ProfileScreen : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        loadUserData()
        setupListeners()
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        // Make motto clickable
        binding.tvMotto.setOnClickListener {
            showEditProfileDialog()
        }

        binding.btnEditProfile.setOnClickListener {
            showEditProfileDialog()
        }

        binding.btnGoToSettings.setOnClickListener {
            startActivity(Intent(this, SettingsScreen::class.java))
        }
    }

    private fun loadUserData() {
        val userId = auth.currentUser?.uid ?: return
        val email = auth.currentUser?.email ?: ""

        // Set email
        binding.tvEmail.text = email

        // Load user data from Firestore
        firestore.collection("users")


            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val name = document.getString("name") ?: "User"
                    val createdAt = document.getLong("createdAt") ?: System.currentTimeMillis()
                    val motto = document.getString("motto") ?: "One day at a time"

                    // Set name
                    binding.tvUserName.text = name
                    binding.tvName.text = name

                    // Set initial (first letter of name)
                    val initial = name.firstOrNull()?.uppercaseChar()?.toString() ?: "U"
                    binding.tvInitial.text = initial

                    // Set motto
                    binding.tvMotto.text = motto

                    // Format joined date
                    val joinedDate = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date(createdAt))

                    val fullJoinedDate = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(Date(createdAt))
                    binding.tvJoinedDate.text = fullJoinedDate
                }
            }
    }

    private fun showEditProfileDialog() {
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(50, 20, 50, 20)

        // Name input
        val nameInput = EditText(this)
        nameInput.hint = "Name"
        nameInput.setText(binding.tvName.text)
        layout.addView(nameInput)

        // Motto input
        val mottoInput = EditText(this)
        mottoInput.hint = "Personal Motto"
        mottoInput.setText(binding.tvMotto.text)
        layout.addView(mottoInput)

        AlertDialog.Builder(this)
            .setTitle("Edit Profile")
            .setView(layout)
            .setPositiveButton("Save") { _, _ ->
                val newName = nameInput.text.toString().trim()
                val newMotto = mottoInput.text.toString().trim()

                if (newName.isNotEmpty()) {
                    updateProfile(newName, newMotto)
                } else {
                    Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun updateProfile(name: String, motto: String) {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("users")
            .document(userId)
            .update(
                mapOf(
                    "name" to name,
                    "motto" to motto
                )
            )
            .addOnSuccessListener {
                Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show()
                loadUserData() // Reload data
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()
            }
    }
}