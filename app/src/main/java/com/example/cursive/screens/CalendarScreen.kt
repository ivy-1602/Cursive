package com.example.cursive.screens

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.cursive.R
import com.example.cursive.databinding.ActivityCalendarBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class CalendarScreen : AppCompatActivity() {

    private lateinit var binding: ActivityCalendarBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private var selectedEntryId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalendarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        setupListeners()
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth, 0, 0, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            checkEntryForDate(calendar.timeInMillis)
        }

        binding.btnViewEntry.setOnClickListener {
            openEntryView()
        }

        // Make entire card clickable
        binding.cardSelectedDate.setOnClickListener {
            openEntryView()
        }
    }

    private fun openEntryView() {
        if (selectedEntryId != null) {
            val intent = Intent(this, ViewEntryScreen::class.java)
            intent.putExtra("ENTRY_ID", selectedEntryId)
            startActivity(intent)
        } else {
            Toast.makeText(this, "No entry found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkEntryForDate(selectedDate: Long) {
        val userId = auth.currentUser?.uid ?: return
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val selectedDateStr = dateFormat.format(Date(selectedDate))

        firestore.collection("entries")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                var foundEntry = false
                var foundMood = "NEUTRAL"

                for (doc in documents) {
                    val entryDate = doc.getLong("date") ?: 0
                    val entryDateStr = dateFormat.format(Date(entryDate))

                    if (entryDateStr == selectedDateStr) {
                        foundEntry = true
                        selectedEntryId = doc.id
                        foundMood = doc.getString("mood") ?: "NEUTRAL"
                        break
                    }
                }

                if (foundEntry) {
                    showEntryInfo(selectedDate, foundMood)
                } else {
                    selectedEntryId = null
                    binding.cardSelectedDate.visibility = android.view.View.GONE
                    Toast.makeText(this, "No entry for this date", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun showEntryInfo(date: Long, mood: String) {
        val dateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
        binding.tvSelectedDate.text = dateFormat.format(Date(date))

        val (moodText, moodColorRes) = when (mood) {
            "HAPPY" -> Pair("Happy", R.color.mood_happy)
            "CALM" -> Pair("Calm", R.color.mood_calm)
            "NEUTRAL" -> Pair("Neutral", R.color.mood_neutral)
            "SAD" -> Pair("Sad", R.color.mood_sad)
            "ANXIOUS" -> Pair("Anxious", R.color.mood_anxious)
            else -> Pair("Neutral", R.color.mood_neutral)
        }

        // Set mood text (no emoji, no "Mood:" prefix)
        binding.tvSelectedMood.text = moodText

        // Set mood circle color
        val moodColor = ContextCompat.getColor(this, moodColorRes)
        binding.viewMoodCircle.setBackgroundResource(R.drawable.mood_circle)
        val drawable = binding.viewMoodCircle.background as? android.graphics.drawable.GradientDrawable
        drawable?.setColor(moodColor)

        // Show card with clean background (no color tint)
        binding.cardSelectedDate.setCardBackgroundColor(
            ContextCompat.getColor(this, R.color.card_background)
        )
        binding.cardSelectedDate.visibility = android.view.View.VISIBLE
    }
}
