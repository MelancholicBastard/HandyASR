package com.melancholicbastard.handyasr.presentation.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.melancholicbastard.handyasr.presentation.screen.TabScreen

@Composable
fun NavBackHandler(
    navController: NavHostController,
    startTabGraphRoute: String,
    startTabScreenRoute: String,
    onExit: () -> Unit
) {
    BackHandler {
        val currentRoute = navController.currentBackStackEntry?.destination?.route

        val currentGraphRoute = navController.currentBackStackEntry
            ?.destination?.parent?.route
        val isOnTabRoot = currentRoute != null &&
                currentRoute == when (currentGraphRoute) {
            TabScreen.Recorder.graphRoute -> TabScreen.Recorder.route
            TabScreen.History.graphRoute -> TabScreen.History.route
            TabScreen.Settings.graphRoute -> TabScreen.Settings.route
            else -> null
        }

        when {
            !isOnTabRoot && navController.previousBackStackEntry != null -> {
                navController.popBackStack()
            }

            isOnTabRoot && currentRoute != startTabScreenRoute -> {
                navController.navigate(startTabGraphRoute) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }

            else -> {
                onExit()
            }
        }
    }
}
