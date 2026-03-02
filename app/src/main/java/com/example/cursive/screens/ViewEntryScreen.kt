package com.example.cursive.screens

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cursive.databinding.ActivityViewEntryBinding
import com.example.cursive.models.JournalEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class ViewEntryScreen : AppCompatActivity() {

    private lateinit var binding: ActivityViewEntryBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var entry: JournalEntry

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()

        // Get entry ID from intent
        val entryId = intent.getStringExtra("ENTRY_ID") ?: run {
            Toast.makeText(this, "Error loading entry", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadEntry(entryId)
        setupListeners()
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnEdit.setOnClickListener {
            val intent = android.content.Intent(this, AddEntryScreen::class.java)
            intent.putExtra("ENTRY_ID", entry.id)
            startActivity(intent)
            finish()
        }

        binding.btnDelete.setOnClickListener {
            showDeleteConfirmation()
        }
    }

    private fun loadEntry(entryId: String) {
        firestore.collection("entries")
            .document(entryId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    entry = document.toObject(JournalEntry::class.java) ?: return@addOnSuccessListener
                    displayEntry() // Call without arguments
                } else {
                    Toast.makeText(this, "Entry not found", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load entry", Toast.LENGTH_SHORT).show()
                finish()
            }
    }

    private fun displayEntry() {
        // Format and display date
        val dateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
        binding.tvEntryDate.text = dateFormat.format(Date(entry.date))

        // Display mood
        val (emoji, label) = when (entry.mood) {
            "HAPPY" -> Pair("😊", "Happy")
            "CALM" -> Pair("😌", "Calm")
            "NEUTRAL" -> Pair("😐", "Neutral")
            "SAD" -> Pair("😔", "Sad")
            "ANXIOUS" -> Pair("😟", "Anxious")
            else -> Pair("😐", "Neutral")
        }
        binding.tvMoodEmoji.text = emoji
        binding.tvMoodLabel.text = label

        // Display title, free write, and gratitude
        binding.tvTitleOfTheDay.text = entry.titleOfTheDay.ifEmpty { "—" }
        binding.tvWhatToWrite.text = entry.whatToWrite.ifEmpty { "—" }
        binding.tvGratitude.text = entry.gratitude.ifEmpty { "—" }

        // Display reflections
        binding.tvMadeYouSmile.text = entry.madeYouSmile.ifEmpty { "—" }
        binding.tvChallenged.text = entry.challenged.ifEmpty { "—" }
        binding.tvQuietlyHopingFor.text = entry.quietlyHopingFor.ifEmpty { "—" }
        binding.tvImpactedYou.text = entry.impactedYou.ifEmpty { "—" }
        binding.tvLookingForward.text = entry.lookingForward.ifEmpty { "—" }

        // Photo (if exists)
        if (entry.photoUrl.isNotEmpty()) {
            binding.ivPhoto.visibility = View.VISIBLE
            // Load photo with Glide if you have it
            // Glide.with(this).load(entry.photoUrl).into(binding.ivPhoto)
        } else {
            binding.ivPhoto.visibility = View.GONE
        }
    }

    private fun showDeleteConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Delete Entry")
            .setMessage("Are you sure you want to delete this entry? This cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                deleteEntry()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteEntry() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        firestore.collection("entries")
            .document(entry.id)
            .delete()
            .addOnSuccessListener {
                // Update user stats
                updateUserStats(userId)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to delete entry", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateUserStats(userId: String) {
        firestore.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                val totalEntries = document.getLong("totalEntries")?.toInt() ?: 0
                val newTotal = maxOf(0, totalEntries - 1)

                firestore.collection("users")
                    .document(userId)
                    .update("totalEntries", newTotal)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Entry deleted", Toast.LENGTH_SHORT).show()
                        finish()
                    }
            }
    }
}