package com.example.habittracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.*
import com.example.habittracker.data.HabitDatabaseHelper
import com.example.habittracker.navigation.Screen
import com.example.habittracker.ui.theme.HabitTrackerTheme
import com.example.habittracker.screens.*


class ComposeMainActivity : ComponentActivity(){
    private lateinit var dbHelper: HabitDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHelper = HabitDatabaseHelper(this)

        setContent {
            HabitTrackerTheme {
                MainScreen(dbHelper)
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(dbHelper: HabitDatabaseHelper){
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val upperNavItems = listOf(
        UpperNavItem("Home", Screen.Home.route, Icons.Default.Home),
        UpperNavItem("Statistics", Screen.Statistics.route, Icons.Default.BarChart),
        UpperNavItem("Calendar", Screen.Calendar.route, Icons.Default.CalendarMonth),
        UpperNavItem("Profile", Screen.Profile.route, Icons.Default.Person)

    )

    Scaffold(
        topBar = {
            Column{

                TopAppBar(
                    title = {
                        Text(
                            when(currentDestination?.route){
                                Screen.Home.route -> "Habit Tracker"
                                Screen.Statistics.route -> "Statistics"
                                Screen.Calendar.route -> "Calendar"
                                Screen.Profile.route -> "Profile"
                                Screen.Settings.route -> "Settings"
                                Screen.AddHabit.route -> "Add Habit"
                                else -> "Habit Detail"
                            }
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    navigationIcon = {
                        if (currentDestination?.route !in upperNavItems.map {it.route}){
                            IconButton(onClick = {navController.popBackStack()}) {
                                Icon(
                                    Icons.Default.ArrowBackIosNew,
                                    contentDescription = "Back",
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    },
                    actions = {
                        if (currentDestination?.route == Screen.Home.route){
                            IconButton(onClick = {navController.navigate(Screen.Settings.route)}) {
                                Icon(
                                    Icons.Default.Settings,
                                    contentDescription = "Settings",
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }
                )

                if (currentDestination?.route in upperNavItems.map {it.route}){
                    PrimaryScrollableTabRow(
                        selectedTabIndex = upperNavItems.indexOfFirst{
                            it.route == currentDestination?.route
                        }.takeIf { it >= 0 } ?: 0,
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        edgePadding = 16.dp
                    ) {
                        upperNavItems.forEach { item ->
                            Tab(
                                selected = currentDestination?.route == item.route,
                                onClick = {
                                    navController.navigate(item.route){
                                        popUpTo(Screen.Home.route){
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                text = {Text(item.label)},
                                icon = {
                                    Icon(
                                        item.icon,
                                        contentDescription = item.label,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                            )
                        }
                    }
                }
            }
        }
    ) {
        paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ){
            composable(Screen.Home.route){
                HomeScreen(
                    dbHelper = dbHelper,
                    onNavigateToAddHabit = { navController.navigate(Screen.AddHabit.route) },
                    onNavigateToHabitDetail = { habitName ->
                        navController.navigate(Screen.HabitDetail.createRoute(habitName))
                    }
                )
            }

            composable(Screen.AddHabit.route) {
                AddHabitScreen(
                    dbHelper = dbHelper,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.HabitDetail.route) { backStackEntry ->
                val habitName = backStackEntry.arguments?.getString("habitName") ?: ""
                HabitDetailScreen(
                    habitName = habitName,
                    dbHelper = dbHelper,
                    onNavigateBack = { navController.popBackStack() },
                    onDeleteHabit = { navController.popBackStack() }
                )
            }

            composable(Screen.Statistics.route) {
                StatisticsScreen(dbHelper = dbHelper)
            }

            composable(Screen.Calendar.route) {
                CalendarScreen(dbHelper = dbHelper)
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    dbHelper = dbHelper,
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}

data class UpperNavItem(
    val label: String,
    val route: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

