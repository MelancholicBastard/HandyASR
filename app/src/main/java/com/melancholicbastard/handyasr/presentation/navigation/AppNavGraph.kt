package com.melancholicbastard.handyasr.presentation.navigation

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.melancholicbastard.handyasr.presentation.screen.Screen
import com.melancholicbastard.handyasr.presentation.screen.history.HistoryScreen
import com.melancholicbastard.handyasr.presentation.screen.recorder.RecorderScreen
import com.melancholicbastard.handyasr.presentation.screen.settings.SettingsScreen
import com.melancholicbastard.handyasr.presentation.viewmodel.RecorderViewModel
import com.melancholicbastard.handyasr.presentation.viewmodel.RecorderViewModelFactory

@Composable
fun AppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = Screen.Recorder.route,
    activity: ComponentActivity,
    requestPermission: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.Recorder.route) {
            val recorderViewModel: RecorderViewModel = viewModel(
                viewModelStoreOwner = activity,
                factory = RecorderViewModelFactory()
            )
            RecorderScreen(
                viewModel = recorderViewModel,
                requestPermission = requestPermission
            )
        }
        composable(Screen.History.route) {
            HistoryScreen()
        }
        composable(Screen.Settings.route) {
            SettingsScreen()
        }
    }
}
