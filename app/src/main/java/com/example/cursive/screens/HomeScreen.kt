package com.example.cursive.screens

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cursive.R
import com.example.cursive.adapters.EntryAdapter
import com.example.cursive.databinding.ActivityHomeBinding
import com.example.cursive.models.JournalEntry
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class HomeScreen : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var toggle: ActionBarDrawerToggle
    private var todayEntryId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        setupToolbar()
        setupNavigationDrawer()
        setupListeners()
        loadUserData()
        updateDate()
        checkTodayEntry()
        loadRecentEntries()
    }

    override fun onResume() {
        super.onResume()
        // Reload data when returning to home screen
        loadRecentEntries()
        checkTodayEntry()
        loadUserData()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Setup toggle with cream colored icon
        toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        // Make hamburger icon cream colored so it's visible
        toggle.drawerArrowDrawable.color = resources.getColor(R.color.cream, null)

        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    private fun setupNavigationDrawer() {
        binding.navigationView.setNavigationItemSelectedListener(this)

        // Update header with user info
        val headerView = binding.navigationView.getHeaderView(0)
        val tvUserName = headerView.findViewById<android.widget.TextView>(R.id.tvUserName)
        val tvUserEmail = headerView.findViewById<android.widget.TextView>(R.id.tvUserEmail)
        val tvInitial = headerView.findViewById<android.widget.TextView>(R.id.tvUserInitial)
        val user = auth.currentUser
        tvUserEmail.text = user?.email ?: ""

        // Load user name and initial
        loadUserNameAndInitial(tvUserName,  tvInitial)
    }

    private fun loadUserNameAndInitial(tvUserName: android.widget.TextView, tvHeaderInitial: android.widget.TextView) {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val name = document.getString("name") ?: "User"
                    tvUserName.text = name

                    // Set initial (first letter of name)
                    val initial = name.firstOrNull()?.uppercaseChar()?.toString() ?: "U"
                    tvHeaderInitial.text = initial
                }
            }
    }  
      
      
    private fun setupListeners()  {
        // Streak counter click - opens Analytics
        binding.streakContainer.setOnClickListener {
            startActivity(Intent(this, AnalyticsScreen::class.java))
        }

        // Add Entry button - passes entry ID if editing
        binding.btnAddEntry.setOnClickListener {
            val intent = Intent(this, AddEntryScreen::class.java)
            // Pass entry ID if editing today's entry
            if (todayEntryId != null) {
                intent.putExtra("ENTRY_ID", todayEntryId)
            }
            startActivity(intent)
        }

        // Calendar FAB
        binding.fabCalendar.setOnClickListener {
            startActivity(Intent(this, CalendarScreen::class.java))
        }
    }

    private fun loadUserData() {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val currentStreak = document.getLong("currentStreak")?.toInt() ?: 0
                    binding.tvStreak.text = currentStreak.toString()
                }
            }
    }

    private fun loadUserName(textView: android.widget.TextView) {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val name = document.getString("name") ?: "User"
                    textView.text = name
                }
            }
    }

    private fun updateDate() {
        val dateFormat = SimpleDateFormat("EEEE, MMMM dd", Locale.getDefault())
        val date = dateFormat.format(Date())

        // Set date
        binding.tvDate.text = date

        // Get user's first name for greeting
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val fullName = document.getString("name") ?: ""
                    val firstName = fullName.split(" ").firstOrNull() ?: fullName
                    binding.tvGreeting.text = "Hi $firstName, this space is yours."
                }
            }
    }

    private fun checkTodayEntry() {
        val userId = auth.currentUser?.uid ?: return
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        firestore.collection("entries")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                var hasTodayEntry = false

                for (doc in documents) {
                    val entryDate = doc.getLong("date") ?: 0
                    val entryDateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        .format(Date(entryDate))

                    if (entryDateStr == today) {
                        hasTodayEntry = true
                        todayEntryId = doc.id // Store the entry ID
                        val mood = doc.getString("mood") ?: "NEUTRAL"
                        showTodayEntry(mood)
                        break
                    }
                }

                if (!hasTodayEntry) {
                    todayEntryId = null
                    binding.tvTodayStatus.text = "You haven't written today yet..."
                    binding.btnAddEntry.text = "Write Today's Entry"

                    // Reset card to default color
                    binding.cardTodayEntry.setCardBackgroundColor(resources.getColor(R.color.card_background, null))
                    binding.cardTodayEntry.alpha = 1.0f
                }
            }
    }

    private fun showTodayEntry(mood: String) {
        val (moodEmoji, moodLabel, moodColor) = when (mood) {
            "HAPPY" -> Triple("😊", "Happy", R.color.mood_happy)
            "CALM" -> Triple("😌", "Calm", R.color.mood_calm)
            "NEUTRAL" -> Triple("😐", "Neutral", R.color.mood_neutral)
            "SAD" -> Triple("😔", "Sad", R.color.mood_sad)
            "ANXIOUS" -> Triple("😟", "Anxious", R.color.mood_anxious)
            else -> Triple("😐", "Neutral", R.color.mood_neutral)
        }

        // Show simple flower icon instead of text
        val flowerIcon = "✿"

        // Update status with flower colored by mood
        val color = androidx.core.content.ContextCompat.getColor(this, moodColor)
        val statusText = android.text.SpannableString("$flowerIcon  Today's Reflection")
        statusText.setSpan(
            android.text.style.ForegroundColorSpan(color),
            0,
            1, // Just color the flower
            android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.tvTodayStatus.text = statusText
        binding.btnAddEntry.text = "Edit Today's Entry"

        // No background color tint - keep it clean
    }

    private fun loadRecentEntries() {
        val userId = auth.currentUser?.uid ?: run {
            return
        }

        firestore.collection("entries")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                // Convert to list and sort manually (avoids Firestore index requirement)
                val entries = documents.mapNotNull { doc ->
                    try {
                        doc.toObject(JournalEntry::class.java)
                    } catch (e: Exception) {
                        null
                    }
                }.sortedByDescending { it.date }.take(5)

                if (entries.isNotEmpty()) {
                    setupRecyclerView(entries)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error loading entries: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupRecyclerView(entries: List<JournalEntry>) {
        binding.rvRecentEntries.layoutManager = LinearLayoutManager(this)
        binding.rvRecentEntries.adapter = EntryAdapter(entries) { entry ->
            // Launch ViewEntryScreen
            val intent = Intent(this, ViewEntryScreen::class.java)
            intent.putExtra("ENTRY_ID", entry.id)
            startActivity(intent)
        }

        // Make sure RecyclerView is visible
        binding.rvRecentEntries.visibility = android.view.View.VISIBLE
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            }
            R.id.nav_entries -> {
                // FIXED: Navigate to AllEntriesScreen
                startActivity(Intent(this, AllEntriesScreen::class.java))
            }
            R.id.nav_analytics -> {
                startActivity(Intent(this, AnalyticsScreen::class.java))
            }
            R.id.nav_profile -> {
                startActivity(Intent(this, ProfileScreen::class.java))
            }
            R.id.nav_settings -> {
                startActivity(Intent(this, SettingsScreen::class.java))
            }
            R.id.nav_logout -> {
                logout()
            }
        }

        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun logout() {
        android.app.AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes") { _, _ ->
                auth.signOut()
                startActivity(Intent(this, LoginScreen::class.java))
                finish()
            }
            .setNegativeButton("No", null)
            .show()
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
