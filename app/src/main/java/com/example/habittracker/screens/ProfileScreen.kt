package com.example.habittracker.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.habittracker.data.HabitDatabaseHelper
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ProfileScreen(
    dbHelper: HabitDatabaseHelper,
    onNavigateToSettings: () -> Unit
) {
    val allHabits = remember { dbHelper.getAllHabits() }
    val stats = remember { dbHelper.getHabitStatistics() }
    val uniqueDays = remember { allHabits.map { it.date }.distinct().size }

    // Calculate streak (consecutive days)
    val streak = remember {
        val sortedDates = allHabits.map { it.date }.distinct().sorted()
        if (sortedDates.isEmpty()) return@remember 0

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        var currentStreak = 1

        for (i in sortedDates.size - 1 downTo 1) {
            val current = dateFormat.parse(sortedDates[i])
            val previous = dateFormat.parse(sortedDates[i - 1])

            val daysDiff = ((current?.time ?: 0) - (previous?.time ?: 0)) / (1000 * 60 * 60 * 24)

            if (daysDiff == 1L) {
                currentStreak++
            } else {
                break
            }
        }
        currentStreak
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile Avatar
        Surface(
            modifier = Modifier.size(100.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Habit Tracker User",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            "Building better habits daily",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Achievement Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AchievementCard(
                title = "Current Streak",
                value = "$streak",
                subtitle = "days",
                icon = Icons.Default.LocalFireDepartment,
                modifier = Modifier.weight(1f)
            )

            AchievementCard(
                title = "Active Days",
                value = "$uniqueDays",
                subtitle = "days",
                icon = Icons.Default.CalendarMonth,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AchievementCard(
                title = "Total Habits",
                value = "${allHabits.size}",
                subtitle = "completed",
                icon = Icons.Default.CheckCircle,
                modifier = Modifier.weight(1f)
            )

            AchievementCard(
                title = "Unique Habits",
                value = "${stats.size}",
                subtitle = "tracked",
                icon = Icons.Default.Category,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Motivational Quote
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "\"You are wanted in this space. So take it\"",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "- Lj <3",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Settings Button
        FilledTonalButton(
            onClick = onNavigateToSettings,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Settings, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Settings")
        }
    }
}

@Composable
fun AchievementCard(
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}