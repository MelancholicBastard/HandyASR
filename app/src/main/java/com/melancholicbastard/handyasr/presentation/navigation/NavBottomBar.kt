package com.melancholicbastard.handyasr.presentation.navigation

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.melancholicbastard.handyasr.presentation.screen.Screen


@Composable
fun NavBottomBar(navController: NavHostController) {
    val items = listOf(Screen.Recorder, Screen.History, Screen.Settings)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        items.forEach { screen ->
            val iconVector = when (screen) {
                Screen.Recorder -> Icons.Default.Mic
                Screen.History -> Icons.Default.CalendarMonth
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
