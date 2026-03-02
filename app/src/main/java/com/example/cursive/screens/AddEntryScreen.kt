package com.example.cursive.screens

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cursive.databinding.ActivityAddEntryBinding
import com.example.cursive.models.JournalEntry
import com.example.cursive.models.Mood
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class AddEntryScreen : AppCompatActivity() {

    private lateinit var binding: ActivityAddEntryBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private var selectedMood: Mood = Mood.NEUTRAL
    private var isEditMode = false
    private var existingEntryId: String? = null
    private var existingEntryTimestamp: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        existingEntryId = intent.getStringExtra("ENTRY_ID")
        isEditMode = existingEntryId != null

        updateDate()
        setupMoodButtons()
        setupListeners()

        if (isEditMode && existingEntryId != null) {
            loadExistingEntry(existingEntryId!!)
        }
    }

    private fun loadExistingEntry(entryId: String) {
        binding.progressBar.visibility = android.view.View.VISIBLE

        firestore.collection("entries")
            .document(entryId)
            .get()
            .addOnSuccessListener { document ->
                binding.progressBar.visibility = android.view.View.GONE

                if (document.exists()) {
                    val entry = document.toObject(JournalEntry::class.java) ?: return@addOnSuccessListener

                    existingEntryTimestamp = entry.date

                    binding.etTitleOfTheDay.setText(entry.titleOfTheDay)
                    binding.etWhatToWrite.setText(entry.whatToWrite)
                    binding.etGratitude.setText(entry.gratitude)
                    binding.etMadeYouSmile.setText(entry.madeYouSmile)
                    binding.etChallenged.setText(entry.challenged)
                    binding.etQuietlyHopingFor.setText(entry.quietlyHopingFor)
                    binding.etImpactedYou.setText(entry.impactedYou)
                    binding.etLookingForward.setText(entry.lookingForward)

                    val mood = Mood.valueOf(entry.mood)
                    when (mood) {
                        Mood.HAPPY -> selectMood(Mood.HAPPY, binding.btnMoodHappy)
                        Mood.CALM -> selectMood(Mood.CALM, binding.btnMoodCalm)
                        Mood.NEUTRAL -> selectMood(Mood.NEUTRAL, binding.btnMoodNeutral)
                        Mood.SAD -> selectMood(Mood.SAD, binding.btnMoodSad)
                        Mood.ANXIOUS -> selectMood(Mood.ANXIOUS, binding.btnMoodAnxious)
                    }

                    binding.btnSaveEntry.text = "Update Entry"
                }
            }
            .addOnFailureListener { e ->
                binding.progressBar.visibility = android.view.View.GONE
                Toast.makeText(this, "Failed to load entry: ${e.message}", Toast.LENGTH_SHORT).show()
                finish()
            }
    }

    private fun updateDate() {
        val dateFormat = SimpleDateFormat("EEEE, MMMM dd", Locale.getDefault())
        binding.tvEntryDate.text = dateFormat.format(Date())
    }

    private fun setupMoodButtons() {
        selectMood(Mood.NEUTRAL, binding.btnMoodNeutral)

        binding.btnMoodHappy.setOnClickListener {
            selectMood(Mood.HAPPY, binding.btnMoodHappy)
        }
        binding.btnMoodCalm.setOnClickListener {
            selectMood(Mood.CALM, binding.btnMoodCalm)
        }
        binding.btnMoodNeutral.setOnClickListener {
            selectMood(Mood.NEUTRAL, binding.btnMoodNeutral)
        }
        binding.btnMoodSad.setOnClickListener {
            selectMood(Mood.SAD, binding.btnMoodSad)
        }
        binding.btnMoodAnxious.setOnClickListener {
            selectMood(Mood.ANXIOUS, binding.btnMoodAnxious)
        }
    }

    private fun selectMood(mood: Mood, button: com.google.android.material.button.MaterialButton) {
        selectedMood = mood

        binding.btnMoodHappy.strokeWidth = 2
        binding.btnMoodCalm.strokeWidth = 2
        binding.btnMoodNeutral.strokeWidth = 2
        binding.btnMoodSad.strokeWidth = 2
        binding.btnMoodAnxious.strokeWidth = 2

        button.strokeWidth = 6
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnAddPhoto.setOnClickListener {
            Toast.makeText(this, "Photo upload coming soon!", Toast.LENGTH_SHORT).show()
        }

        binding.btnSaveEntry.setOnClickListener {
            saveEntry()
        }
    }

    private fun saveEntry() {
        val titleOfTheDay = binding.etTitleOfTheDay.text.toString().trim()
        val whatToWrite = binding.etWhatToWrite.text.toString().trim()
        val gratitude = binding.etGratitude.text.toString().trim()
        val madeYouSmile = binding.etMadeYouSmile.text.toString().trim()
        val challenged = binding.etChallenged.text.toString().trim()
        val quietlyHopingFor = binding.etQuietlyHopingFor.text.toString().trim()
        val impactedYou = binding.etImpactedYou.text.toString().trim()
        val lookingForward = binding.etLookingForward.text.toString().trim()

        if (titleOfTheDay.isEmpty() && whatToWrite.isEmpty() && gratitude.isEmpty() && madeYouSmile.isEmpty()) {
            Toast.makeText(this, "Please fill in at least one field", Toast.LENGTH_SHORT).show()
            return
        }

        binding.progressBar.visibility = android.view.View.VISIBLE
        binding.btnSaveEntry.isEnabled = false

        val userId = auth.currentUser?.uid ?: return

        val entryId = if (isEditMode && existingEntryId != null) {
            existingEntryId!!
        } else {
            firestore.collection("entries").document().id
        }

        val timestamp = if (isEditMode && existingEntryTimestamp != null) {
            existingEntryTimestamp!!
        } else {
            System.currentTimeMillis()
        }

        val entryData = hashMapOf(
            "id" to entryId,
            "userId" to userId,
            "date" to timestamp,
            "dateReadable" to getReadableDate(timestamp),
            "timeReadable" to getReadableTime(timestamp),
            "dateTimeReadable" to getReadableDateTime(timestamp),
            "mood" to selectedMood.name,
            "titleOfTheDay" to titleOfTheDay,
            "whatToWrite" to whatToWrite,
            "gratitude" to gratitude,
            "madeYouSmile" to madeYouSmile,
            "challenged" to challenged,
            "quietlyHopingFor" to quietlyHopingFor,
            "impactedYou" to impactedYou,
            "lookingForward" to lookingForward,
            "photoUrl" to ""
        )

        firestore.collection("entries")
            .document(entryId)
            .set(entryData)
            .addOnSuccessListener {
                if (isEditMode) {
                    binding.progressBar.visibility = android.view.View.GONE
                    Toast.makeText(this, "Entry updated! 📝", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    updateUserStreak(userId)
                }
            }
            .addOnFailureListener { e ->
                binding.progressBar.visibility = android.view.View.GONE
                binding.btnSaveEntry.isEnabled = true
                Toast.makeText(this, "Failed to save entry: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun getReadableDate(timestamp: Long): String {
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return dateFormat.format(Date(timestamp))
    }

    private fun getReadableTime(timestamp: Long): String {
        val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        return timeFormat.format(Date(timestamp))
    }

    private fun getReadableDateTime(timestamp: Long): String {
        val dateTimeFormat = SimpleDateFormat("dd MMM yyyy, h:mm a", Locale.getDefault())
        return dateTimeFormat.format(Date(timestamp))
    }

    private fun updateUserStreak(userId: String) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = dateFormat.format(Date())
        val yesterday = dateFormat.format(Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000))

        firestore.collection("entries")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                val entryDates = documents.mapNotNull { doc ->
                    val timestamp = doc.getLong("date") ?: return@mapNotNull null
                    val entryDate = dateFormat.format(Date(timestamp))
                    if (entryDate != today) entryDate else null
                }.toSet()

                firestore.collection("users")
                    .document(userId)
                    .get()
                    .addOnSuccessListener { userDoc ->
                        val currentStreak = userDoc.getLong("currentStreak")?.toInt() ?: 0
                        val longestStreak = userDoc.getLong("longestStreak")?.toInt() ?: 0
                        val totalEntries = userDoc.getLong("totalEntries")?.toInt() ?: 0

                        val wroteYesterday = entryDates.contains(yesterday)

                        when {
                            // First ever entry
                            totalEntries == 0 -> {
                                saveStreakUpdate(userId, 1, 1, 1, "Entry saved! Streak started: 1 🔥")
                            }

                            // Wrote yesterday - continue streak
                            wroteYesterday -> {
                                val newStreak = currentStreak + 1
                                val newLongest = maxOf(longestStreak, newStreak)
                                saveStreakUpdate(userId, newStreak, newLongest, totalEntries + 1,
                                    "Entry saved! Streak: $newStreak 🔥")
                            }

                            // Missed yesterday - reset streak
                            else -> {
                                val newLongest = maxOf(longestStreak, currentStreak)
                                saveStreakUpdate(userId, 1, newLongest, totalEntries + 1,
                                    "Entry saved! Streak reset to 1 🔥")
                            }
                        }
                    }
                    .addOnFailureListener { e ->
                        handleStreakError(e)
                    }
            }
            .addOnFailureListener { e ->
                handleStreakError(e)
            }
    }

    private fun saveStreakUpdate(userId: String, newStreak: Int, newLongest: Int, newTotal: Int, message: String) {
        firestore.collection("users")
            .document(userId)
            .update(
                mapOf(
                    "currentStreak" to newStreak,
                    "longestStreak" to newLongest,
                    "totalEntries" to newTotal
                )
            )
            .addOnSuccessListener {
                binding.progressBar.visibility = android.view.View.GONE
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                finish()
            }
            .addOnFailureListener { e ->
                handleStreakError(e)
            }
    }

    private fun handleStreakError(e: Exception) {
        binding.progressBar.visibility = android.view.View.GONE
        binding.btnSaveEntry.isEnabled = true
        Toast.makeText(this, "Error updating streak: ${e.message}", Toast.LENGTH_SHORT).show()
        finish()
    }
}