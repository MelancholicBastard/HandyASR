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
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.melancholicbastard.handyasr.presentation.screen.TabScreen

@Composable
fun NavBottomBar(navController: NavHostController) {
    val items = listOf(TabScreen.Recorder, TabScreen.History, TabScreen.Settings)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val activeTabRoute = when {
        currentDestination?.hierarchy?.any { it.route == TabScreen.Recorder.graphRoute } == true -> TabScreen.Recorder.route
        currentDestination?.hierarchy?.any { it.route == TabScreen.History.graphRoute } == true -> TabScreen.History.route
        currentDestination?.hierarchy?.any { it.route == TabScreen.Settings.graphRoute } == true -> TabScreen.Settings.route
        else -> null
    }

    NavigationBar {
        items.forEach { screen ->
            val iconVector = when (screen) {
                TabScreen.Recorder -> Icons.Default.Mic
                TabScreen.History -> Icons.Default.CalendarMonth
                TabScreen.Settings -> Icons.Default.Settings
            }

            val targetGraphRoute = when (screen) {
                TabScreen.Recorder -> TabScreen.Recorder.graphRoute
                TabScreen.History -> TabScreen.History.graphRoute
                TabScreen.Settings -> TabScreen.Settings.graphRoute
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
                selected = activeTabRoute == screen.route,
                onClick = {
                    if (activeTabRoute == screen.route) {
                        return@NavigationBarItem
                    } else {
                        navController.navigate(targetGraphRoute) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}
