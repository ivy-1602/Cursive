package com.example.cursive.models

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val motto: String = "Change the prophecy.",
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val totalEntries: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)