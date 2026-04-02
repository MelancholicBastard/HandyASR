package com.melancholicbastard.handyasr.presentation.screen

sealed class Screen(val route: String, val label: String) {
    object Recorder : Screen("recorder", "Recorder")
    object History : Screen("history", "History")
    object Settings : Screen("settings", "Settings")
    object Editor : Screen("editor", "Editor") {
        const val IS_NEW_ARG = "isNew"
        val routePattern = "$route/{$IS_NEW_ARG}"
        fun createRoute(isNew: Boolean): String = "$route/$isNew"
    }
}