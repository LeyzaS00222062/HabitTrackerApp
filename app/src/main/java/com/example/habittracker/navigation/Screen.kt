package com.example.habittracker.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object AddHabit : Screen("add_habit")
    object HabitDetail : Screen("habit_detail/{habitName}") {
        fun createRoute(habitName: String) = "habit_detail/$habitName"
    }
    object Statistics : Screen("statistics")
    object Calendar : Screen("calendar")
    object Settings : Screen("settings")
    object Profile : Screen("profile")
}