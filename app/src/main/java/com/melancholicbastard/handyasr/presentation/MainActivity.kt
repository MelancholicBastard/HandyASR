package com.melancholicbastard.handyasr.presentation

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.melancholicbastard.handyasr.presentation.navigation.AppNavGraph
import com.melancholicbastard.handyasr.presentation.navigation.NavBackHandler
import com.melancholicbastard.handyasr.presentation.navigation.NavBottomBar
import com.melancholicbastard.handyasr.presentation.screen.TabScreen
import com.melancholicbastard.handyasr.presentation.ui.HandyASRTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val microphonePermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (!granted) Toast.makeText(
                this,
                "Разрешение не было предоставлено",
                Toast.LENGTH_SHORT
            ).show()
        }
        val requestPermission =
            { microphonePermissionLauncher.launch(Manifest.permission.RECORD_AUDIO) }

        setContent {
            val navController = rememberNavController()

            HandyASRTheme(dynamicColor = false) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = { NavBottomBar(navController = navController) }
                ) { innerPadding ->
                    AppNavGraph(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding),
                        startDestination = TabScreen.Recorder.graphRoute,
                        activity = this,
                        bottomPadding = innerPadding.calculateBottomPadding(),
                        requestPermission = requestPermission
                    )

                    NavBackHandler(
                        navController = navController,
                        startTabGraphRoute = TabScreen.Recorder.graphRoute,
                        startTabScreenRoute = TabScreen.Recorder.route,
                        onExit = { moveTaskToBack(true) }
                    )
                }
            }
        }
    }
}
