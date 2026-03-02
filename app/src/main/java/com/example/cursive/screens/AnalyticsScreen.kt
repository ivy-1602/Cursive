package com.example.cursive.screens

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.cursive.databinding.ActivityAnalyticsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AnalyticsScreen : AppCompatActivity() {

    private lateinit var binding: ActivityAnalyticsBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnalyticsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        setupListeners()
        loadAnalytics()
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun loadAnalytics() {
        val userId = auth.currentUser?.uid ?: return

        // Load user stats
        firestore.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val currentStreak = document.getLong("currentStreak")?.toInt() ?: 0
                    val longestStreak = document.getLong("longestStreak")?.toInt() ?: 0
                    val totalEntries = document.getLong("totalEntries")?.toInt() ?: 0

                    binding.tvCurrentStreak.text = currentStreak.toString()
                    binding.tvLongestStreak.text = longestStreak.toString()
                    binding.tvTotalEntries.text = totalEntries.toString()
                }
            }

        // Load mood distribution
        firestore.collection("entries")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                var happyCount = 0
                var calmCount = 0
                var neutralCount = 0
                var sadCount = 0
                var anxiousCount = 0

                for (doc in documents) {
                    when (doc.getString("mood")) {
                        "HAPPY" -> happyCount++
                        "CALM" -> calmCount++
                        "NEUTRAL" -> neutralCount++
                        "SAD" -> sadCount++
                        "ANXIOUS" -> anxiousCount++
                    }
                }

                binding.tvHappyCount.text = happyCount.toString()
                binding.tvCalmCount.text = calmCount.toString()
                binding.tvNeutralCount.text = neutralCount.toString()
                binding.tvSadCount.text = sadCount.toString()
                binding.tvAnxiousCount.text = anxiousCount.toString()
            }
    }
}