package com.melancholicbastard.handyasr.presentation.navigation

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.melancholicbastard.handyasr.presentation.screen.Screen
import com.melancholicbastard.handyasr.presentation.screen.editor.EditorScreen
import com.melancholicbastard.handyasr.presentation.screen.history.HistoryScreen
import com.melancholicbastard.handyasr.presentation.screen.recorder.RecorderScreen
import com.melancholicbastard.handyasr.presentation.screen.settings.SettingsScreen
import com.melancholicbastard.handyasr.presentation.viewmodel.EditViewModel
import com.melancholicbastard.handyasr.presentation.viewmodel.EditViewModelFactory
import com.melancholicbastard.handyasr.presentation.viewmodel.HistoryViewModel
import com.melancholicbastard.handyasr.presentation.viewmodel.HistoryViewModelFactory
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
            val onOpenEditorForNewRecord : (String) -> Unit = { filePath ->
                navController.navigate(Screen.Editor.createRoute(isNew = true, entity = filePath))
            }
            val recorderViewModel: RecorderViewModel = viewModel(
                viewModelStoreOwner = activity,
                factory = RecorderViewModelFactory(onOpenEditorForNewRecord = onOpenEditorForNewRecord)
            )
            RecorderScreen(
                viewModel = recorderViewModel,
                requestPermission = requestPermission
            )
        }
        composable(Screen.History.route) {
            val onOpenEditorForExistingRecord : (String) -> Unit = { recordId ->
                navController.navigate(Screen.Editor.createRoute(isNew = false, entity = recordId))
            }
            val historyViewModel: HistoryViewModel = viewModel(
                viewModelStoreOwner = activity,
                factory = HistoryViewModelFactory(onOpenEditorForExistingRecord = onOpenEditorForExistingRecord)
            )

            HistoryScreen(
                viewModel = historyViewModel
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen()
        }
        composable(
            route = Screen.Editor.routePattern,
            arguments = listOf(
                navArgument(Screen.Editor.IS_NEW_ARG) {
                    type = NavType.BoolType
                    defaultValue = true
                },
                navArgument(Screen.Editor.ENTITY_KEY) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val isNew = backStackEntry.arguments?.getBoolean(Screen.Editor.IS_NEW_ARG) ?: true
            val entity = backStackEntry.arguments?.getString(Screen.Editor.ENTITY_KEY) ?: ""
            val editViewModel: EditViewModel = viewModel(
                factory = EditViewModelFactory(
                    isNewRecord = isNew,
                    entity = entity
                )
            )
            EditorScreen(
                viewModel = editViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
