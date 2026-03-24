package com.melancholicbastard.handyasr.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.melancholicbastard.handyasr.presentation.screen.history.HistoryScreen
import com.melancholicbastard.handyasr.presentation.screen.recorder.RecorderScreen
import com.melancholicbastard.handyasr.presentation.screen.Screen
import com.melancholicbastard.handyasr.presentation.screen.settings.SettingsScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = Screen.Recorder.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
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
