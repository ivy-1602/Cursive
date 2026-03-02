package com.example.cursive.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cursive.databinding.ItemEntryBinding
import com.example.cursive.models.JournalEntry
import java.text.SimpleDateFormat
import com.example.cursive.R
import java.util.*

class EntryAdapter(
    private var entries: List<JournalEntry>, // Changed from val to var
    private val onEntryClick: (JournalEntry) -> Unit
) : RecyclerView.Adapter<EntryAdapter.EntryViewHolder>() {

    inner class EntryViewHolder(private val binding: ItemEntryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(entry: JournalEntry) {
            // Format date
            val dateFormat = SimpleDateFormat("MMMM dd", Locale.getDefault())
            binding.tvEntryDate.text = dateFormat.format(Date(entry.date))

            // Set mood color
            val moodColor = when (entry.mood) {
                "HAPPY" -> R.color.mood_happy
                "CALM" -> R.color.mood_calm
                "NEUTRAL" -> R.color.mood_neutral
                "SAD" -> R.color.mood_sad
                "ANXIOUS" -> R.color.mood_anxious
                else -> R.color.mood_neutral
            }
            binding.viewMoodColor.setBackgroundResource(moodColor)

            // Make it circular
            binding.viewMoodColor.background = binding.root.context.getDrawable(R.drawable.mood_circle)
            val drawable = binding.viewMoodColor.background as? android.graphics.drawable.GradientDrawable
            drawable?.setColor(binding.root.context.getColor(moodColor))

            // Set preview text - prioritize title, then free write, then smile answer
            val preview = when {
                entry.titleOfTheDay.isNotEmpty() -> entry.titleOfTheDay
                entry.whatToWrite.isNotEmpty() -> entry.whatToWrite.take(100) + if (entry.whatToWrite.length > 100) "..." else ""
                entry.madeYouSmile.isNotEmpty() -> entry.madeYouSmile
                entry.gratitude.isNotEmpty() -> "Grateful for ${entry.gratitude}"
                else -> "Tap to view entry..."
            }
            binding.tvEntryPreview.text = preview

            // Click listener
            binding.root.setOnClickListener {
                onEntryClick(entry)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntryViewHolder {
        val binding = ItemEntryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return EntryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EntryViewHolder, position: Int) {
        holder.bind(entries[position])
    }

    override fun getItemCount() = entries.size

    // Add this function at the end of the class
    fun updateEntries(newEntries: List<JournalEntry>) {
        entries = newEntries
        notifyDataSetChanged()
    }
}