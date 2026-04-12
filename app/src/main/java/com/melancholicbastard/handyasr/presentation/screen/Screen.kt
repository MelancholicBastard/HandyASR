package com.melancholicbastard.handyasr.presentation.screen

import android.net.Uri

sealed class Screen(val route: String, val label: String) {
    object Recorder : Screen("recorder", "Recorder")
    object History : Screen("history", "History")
    object Settings : Screen("settings", "Settings")
    object Editor : Screen("editor", "Editor") {
        const val IS_NEW_ARG = "isNew"
        const val ENTITY_KEY = "entity"
        val routePattern = "$route/{$IS_NEW_ARG}?$ENTITY_KEY={$ENTITY_KEY}"
        fun createRoute(isNew: Boolean, entity: String): String {
            if (isNew) {
                val encodedEntity = Uri.encode(entity)
                return "$route/true?$ENTITY_KEY=$encodedEntity"
            } else {
                return "$route/false?$ENTITY_KEY=$entity"
            }
        }
    }
}