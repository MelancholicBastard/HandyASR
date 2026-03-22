package com.melancholicbastard.handyasr.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.melancholicbastard.handyasr.presentation.screen.HistoryScreen
import com.melancholicbastard.handyasr.presentation.screen.RecorderScreen
import com.melancholicbastard.handyasr.presentation.screen.Screen
import com.melancholicbastard.handyasr.presentation.screen.SettingsScreen
import com.melancholicbastard.handyasr.presentation.ui.HandyASRTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()

            HandyASRTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = { BottomBar(navController = navController) }
                ) { innerPadding ->
                    BackHandler {
                        val startRoute = Screen.Recorder.route
                        val currentRoute = navController.currentBackStackEntry?.destination?.route
                        val hasPrevious = navController.previousBackStackEntry != null

                        if (hasPrevious) {
                            navController.popBackStack()
                        } else {
                            if (currentRoute != null && currentRoute != startRoute) {
                                navController.navigate(startRoute) {
                                    popUpTo(startRoute) { inclusive = true }
                                    launchSingleTop = true
                                }
                            } else {
                                moveTaskToBack(true)
                            }
                        }
                    }

                    NavHost(
                        navController = navController,
                        startDestination = Screen.Recorder.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Screen.Recorder.route) {
                            RecorderScreen()
                        }
                        composable(Screen.History.route) {
                            HistoryScreen()
                        }
                        composable(Screen.Settings.route) {
                            SettingsScreen()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BottomBar(navController: NavHostController) {
    val items = listOf(Screen.Recorder, Screen.History, Screen.Settings)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        items.forEach { screen ->
            val iconVector = when (screen) {
                Screen.Recorder -> Icons.Default.Mic
                Screen.History  -> Icons.Default.CalendarMonth
                Screen.Settings -> Icons.Default.Settings
            }

            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = iconVector,
                        contentDescription = screen.label,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = { Text(screen.label) },
                selected = currentRoute == screen.route,
                onClick = {
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {
                            popUpTo(screen.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }
            )
        }
    }
}