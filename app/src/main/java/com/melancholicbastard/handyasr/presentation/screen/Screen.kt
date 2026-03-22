package com.melancholicbastard.handyasr.presentation.screen

sealed class Screen(val route: String, val label: String) {
    object Recorder : Screen("recorder", "Recorder")
    object History : Screen("history", "History")
    object Settings : Screen("settings", "Settings")
}