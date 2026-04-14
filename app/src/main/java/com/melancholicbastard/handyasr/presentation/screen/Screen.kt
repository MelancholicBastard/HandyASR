package com.melancholicbastard.handyasr.presentation.screen

import android.net.Uri

sealed class TabScreen(val route: String, val graphRoute: String, val label: String) {
    object Recorder : TabScreen("recorder", "recorder_graph", "Recorder")
    object History : TabScreen("history", "history_graph", "History")
    object Settings : TabScreen("settings", "settings_graph", "Settings")
}


object EditorRoutes {
    const val IS_NEW_ARG = "isNew"
    const val ENTITY_KEY = "entity"
    const val ROUTE = "editor"
    val routePattern = "$ROUTE/{$IS_NEW_ARG}?$ENTITY_KEY={$ENTITY_KEY}"
    fun createRoute(isNew: Boolean, entity: String): String {
        if (isNew) {
            val encodedEntity = Uri.encode(entity)
            return "$ROUTE/true?$ENTITY_KEY=$encodedEntity"
        } else {
            return "$ROUTE/false?$ENTITY_KEY=$entity"
        }
    }
}
