package com.example.habittracker.data

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Habit(
    var id: Int = 0,
    val habitName: String,
    val date: String,
    val timestamp: Long = System.currentTimeMillis()
) {
    fun getFormattedTime(): String {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}