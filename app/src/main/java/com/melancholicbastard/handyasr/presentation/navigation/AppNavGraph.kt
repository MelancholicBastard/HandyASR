package com.melancholicbastard.handyasr.presentation.navigation

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.melancholicbastard.handyasr.presentation.screen.EditorRoutes
import com.melancholicbastard.handyasr.presentation.screen.TabScreen
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
    startDestination: String = TabScreen.Recorder.graphRoute,
    activity: ComponentActivity,
    bottomPadding: Dp = 0.dp,
    requestPermission: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        navigation(
            route = TabScreen.Recorder.graphRoute,
            startDestination = TabScreen.Recorder.route
        ) {
            composable(TabScreen.Recorder.route) {
                val onOpenEditorForNewRecord: (String) -> Unit = { filePath ->
                    navController.navigate(
                        EditorRoutes.createRoute(
                            isNew = true,
                            entity = filePath
                        )
                    )
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

            composableForEditorScreen(
                bottomPadding = bottomPadding,
                onBack = { navController.popBackStack() },
                onSaved = {
                    navController.popBackStack()
                    navController.navigate(TabScreen.History.graphRoute) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }

        navigation(
            route = TabScreen.History.graphRoute,
            startDestination = TabScreen.History.route
        ) {
            composable(TabScreen.History.route) {
                val onOpenEditorForExistingRecord: (String) -> Unit = { recordId ->
                    navController.navigate(
                        EditorRoutes.createRoute(
                            isNew = false,
                            entity = recordId
                        )
                    )
                }
                val historyViewModel: HistoryViewModel = viewModel(
                    viewModelStoreOwner = activity,
                    factory = HistoryViewModelFactory(onOpenEditorForExistingRecord = onOpenEditorForExistingRecord)
                )

                HistoryScreen(
                    viewModel = historyViewModel
                )
            }

            composableForEditorScreen(
                bottomPadding = bottomPadding,
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        navigation(
            route = TabScreen.Settings.graphRoute,
            startDestination = TabScreen.Settings.route
        ) {
            composable(TabScreen.Settings.route) {
                SettingsScreen()
            }
        }
    }
}

private fun NavGraphBuilder.composableForEditorScreen(
    bottomPadding: Dp,
    onBack: () -> Unit,
    onSaved: () -> Unit = onBack
) {
    composable(
        route = EditorRoutes.routePattern,
        arguments = listOf(
            navArgument(EditorRoutes.IS_NEW_ARG) { type = NavType.BoolType; defaultValue = true },
            navArgument(EditorRoutes.ENTITY_KEY) { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val isNew = backStackEntry.arguments?.getBoolean(EditorRoutes.IS_NEW_ARG) ?: true
        val entity = backStackEntry.arguments?.getString(EditorRoutes.ENTITY_KEY) ?: ""

        val editViewModel: EditViewModel = viewModel(
            factory = EditViewModelFactory(
                isNewRecord = isNew,
                entity = entity,
                onNodeSaved = onSaved,
                onBackClick = onBack
            )
        )
        EditorScreen(
            viewModel = editViewModel,
            bottomPadding = bottomPadding
        )
    }
}
