package com.example.cursive.models

enum class Mood(val emoji: String, val label: String, val colorRes: Int) {
    HAPPY("😊", "Happy", android.R.color.holo_green_light),
    CALM("😌", "Calm", android.R.color.holo_blue_light),
    NEUTRAL("😐", "Neutral", android.R.color.darker_gray),
    SAD("😔", "Sad", android.R.color.holo_purple),
    ANXIOUS("😟", "Anxious", android.R.color.holo_orange_light)
}