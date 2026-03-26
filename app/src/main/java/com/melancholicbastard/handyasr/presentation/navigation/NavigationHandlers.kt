package com.melancholicbastard.handyasr.presentation.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@Composable
fun NavBackHandler(
    navController: NavHostController,
    startRoute: String,
    onExit: () -> Unit
) {
    BackHandler {
        val currentRoute = navController.currentBackStackEntry?.destination?.route
        val hasPrevious = navController.previousBackStackEntry != null

        if (hasPrevious) {
            navController.popBackStack()
        } else {
            if (currentRoute != null && currentRoute != startRoute) {
                navController.navigate(startRoute) {
                    popUpTo(startRoute) {
                        inclusive = true
                        saveState = true // На всякий случай
                    }
                    launchSingleTop = true
                }
            } else {
                onExit()
            }
        }
    }
}
