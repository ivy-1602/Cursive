package com.example.cursive.models

data class JournalEntry(
    val id: String = "",
    val userId: String = "",
    val date: Long = System.currentTimeMillis(),
    val mood: String = Mood.NEUTRAL.name,
    val titleOfTheDay: String = "",
    val whatToWrite: String = "",
    val gratitude: String = "",
    val madeYouSmile: String = "",
    val challenged: String = "",
    val quietlyHopingFor: String = "",
    val impactedYou: String = "",
    val lookingForward: String = "",
    val photoUrl: String = ""
)