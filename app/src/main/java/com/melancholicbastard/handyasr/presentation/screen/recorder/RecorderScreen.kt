package com.melancholicbastard.handyasr.presentation.screen.recorder

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.melancholicbastard.handyasr.presentation.viewmodel.RecordScreenUIState
import com.melancholicbastard.handyasr.presentation.viewmodel.RecorderViewModel
import com.melancholicbastard.handyasr.presentation.viewmodel.RecorderViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

@Composable
fun RecorderScreen(
    viewModel: RecorderViewModel = viewModel( factory = RecorderViewModelFactory() ),
    requestPermission: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        when (uiState) {
            RecordScreenUIState.IdleUIState -> IdleView(
                viewModel,
                requestPermission
            )
            RecordScreenUIState.StartUIState -> RecordingView(viewModel)
            RecordScreenUIState.PauseUIState -> PauseView(viewModel)
            RecordScreenUIState.ProcessUIState -> ProcessView(viewModel)
            RecordScreenUIState.RedactUIState -> RedactView(viewModel)
        }
    }
}

@Composable
fun RedactView(viewModel: RecorderViewModel) {

    TODO("Not yet implemented")
}

@Composable
fun ProcessView(viewModel: RecorderViewModel) {
    TODO("Not yet implemented")
}

@Composable
fun PauseView(viewModel: RecorderViewModel) {
    TODO("Not yet implemented")
}

@Composable
fun RecordingView(viewModel: RecorderViewModel) {
    Text("Лох")
}

@Composable
fun IdleView(
    viewModel: RecorderViewModel,
    requestPermission: () -> Unit
) {
    val requestForPermission = viewModel.requestForPermission

    LaunchedEffect(requestForPermission) {
        viewModel.requestForPermission.collect { event ->
            requestPermission()
        }
    }

    Text(text = "Нажмите, чтобы начать запись")
    Spacer(modifier = Modifier.height(16.dp))
    IconButton(
        onClick = {
            viewModel.startRecording()
        }
    ) {
        Icon(
            imageVector = Icons.Default.Mic,
            contentDescription = "Start recording"
        )
    }
}