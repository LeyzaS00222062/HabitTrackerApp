package com.example.habittracker

import java.sql.Timestamp

data class Habit(
    var id: Int = 0,
    val habitName: String,
    val date: String,
    val timestamp: Long = System.currentTimeMillis()
)
