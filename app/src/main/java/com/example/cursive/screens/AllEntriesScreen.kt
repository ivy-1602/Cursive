package com.example.cursive.screens

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cursive.adapters.EntryAdapter
import com.example.cursive.databinding.ActivityAllEntriesBinding
import com.example.cursive.models.JournalEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AllEntriesScreen : AppCompatActivity() {

    private lateinit var binding: ActivityAllEntriesBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var adapter: EntryAdapter

    private var allEntries = listOf<JournalEntry>()
    private var filteredEntries = listOf<JournalEntry>()
    private var selectedMood: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllEntriesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        setupToolbar()
        setupRecyclerView()
        setupSearch()
        setupMoodFilters()
        loadAllEntries()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false) // Hide default title

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        adapter = EntryAdapter(emptyList()) { entry ->
            // Navigate to ViewEntryScreen
            val intent = Intent(this, ViewEntryScreen::class.java)
            intent.putExtra("ENTRY_ID", entry.id)
            startActivity(intent)
        }

        binding.recyclerViewAllEntries.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewAllEntries.adapter = adapter
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                filterEntries(s.toString())
            }
        })
    }

    private fun setupMoodFilters() {
        binding.chipAll.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedMood = null
                uncheckOtherChips(binding.chipAll.id)
                filterEntries(binding.etSearch.text.toString())
            }
        }

        binding.chipHappy.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedMood = "HAPPY"
                uncheckOtherChips(binding.chipHappy.id)
                filterEntries(binding.etSearch.text.toString())
            }
        }

        binding.chipCalm.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedMood = "CALM"
                uncheckOtherChips(binding.chipCalm.id)
                filterEntries(binding.etSearch.text.toString())
            }
        }

        binding.chipNeutral.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedMood = "NEUTRAL"
                uncheckOtherChips(binding.chipNeutral.id)
                filterEntries(binding.etSearch.text.toString())
            }
        }

        binding.chipSad.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedMood = "SAD"
                uncheckOtherChips(binding.chipSad.id)
                filterEntries(binding.etSearch.text.toString())
            }
        }

        binding.chipAnxious.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedMood = "ANXIOUS"
                uncheckOtherChips(binding.chipAnxious.id)
                filterEntries(binding.etSearch.text.toString())
            }
        }
    }

    private fun uncheckOtherChips(checkedChipId: Int) {
        if (checkedChipId != binding.chipAll.id) binding.chipAll.isChecked = false
        if (checkedChipId != binding.chipHappy.id) binding.chipHappy.isChecked = false
        if (checkedChipId != binding.chipCalm.id) binding.chipCalm.isChecked = false
        if (checkedChipId != binding.chipNeutral.id) binding.chipNeutral.isChecked = false
        if (checkedChipId != binding.chipSad.id) binding.chipSad.isChecked = false
        if (checkedChipId != binding.chipAnxious.id) binding.chipAnxious.isChecked = false
    }

    private fun loadAllEntries() {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("entries")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                allEntries = documents.mapNotNull { doc ->
                    doc.toObject(JournalEntry::class.java)
                }.sortedByDescending { it.date }

                filteredEntries = allEntries
                updateUI()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to load entries: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun filterEntries(searchText: String) {
        filteredEntries = allEntries.filter { entry ->
            val matchesMood = selectedMood == null || entry.mood == selectedMood
            val matchesSearch = searchText.isEmpty() ||
                    entry.titleOfTheDay.contains(searchText, ignoreCase = true) ||
                    entry.whatToWrite.contains(searchText, ignoreCase = true) ||
                    entry.gratitude.contains(searchText, ignoreCase = true) ||
                    entry.madeYouSmile.contains(searchText, ignoreCase = true)

            matchesMood && matchesSearch
        }

        updateUI()
    }

    private fun updateUI() {
        if (filteredEntries.isEmpty()) {
            binding.recyclerViewAllEntries.visibility = View.GONE
            binding.emptyStateLayout.visibility = View.VISIBLE
            binding.tvEntriesCount.text = "0 entries"
        } else {
            binding.recyclerViewAllEntries.visibility = View.VISIBLE
            binding.emptyStateLayout.visibility = View.GONE
            binding.tvEntriesCount.text = "${filteredEntries.size} ${if (filteredEntries.size == 1) "entry" else "entries"}"
            adapter.updateEntries(filteredEntries)
        }
    }

    override fun onResume() {
        super.onResume()
        // Reload entries when returning from ViewEntryScreen (in case of edits/deletes)
        loadAllEntries()
    }
}