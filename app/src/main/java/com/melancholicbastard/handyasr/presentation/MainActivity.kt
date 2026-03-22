package com.melancholicbastard.handyasr.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.melancholicbastard.handyasr.presentation.navigation.AppNavGraph
import com.melancholicbastard.handyasr.presentation.navigation.NavBackHandler
import com.melancholicbastard.handyasr.presentation.navigation.NavBottomBar
import com.melancholicbastard.handyasr.presentation.screen.Screen
import com.melancholicbastard.handyasr.presentation.ui.HandyASRTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()

            HandyASRTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = { NavBottomBar(navController = navController) }
                ) { innerPadding ->
                    NavBackHandler(
                        navController = navController,
                        startRoute = Screen.Recorder.route,
                        onExit = { moveTaskToBack(true) }
                    )

                    AppNavGraph(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding),
                        startDestination = Screen.Recorder.route
                    )
                }
            }
        }
    }
}
