package com.example.cursive.screens

import android.os.Bundle
import android.text.Html
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.example.cursive.databinding.ActivityAboutBinding

class AboutScreen : AppCompatActivity() {

    private lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        setupContent()
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun setupContent() {
        // Set version info (manually set for now)
        binding.tvVersion.text = "Version 1.0.0"

        // Set italic text for "Cursive" in paragraphs
        binding.tvParagraph3.text = HtmlCompat.fromHtml(
            "<i>Cursive</i> was created for that pause.",
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )

        binding.tvParagraph7.text = HtmlCompat.fromHtml(
            "In a world that constantly asks for your attention, <i>Cursive</i> is a space that gives it back to you.",
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
    }
}