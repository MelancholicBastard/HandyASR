package com.melancholicbastard.handyasr.presentation.screen.recorder

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.melancholicbastard.handyasr.presentation.viewmodel.RecordScreenUIState
import com.melancholicbastard.handyasr.presentation.viewmodel.RecorderViewModel

@Composable
fun RecorderScreen(
    viewModel: RecorderViewModel,
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
    Text("Test")
}

@Composable
fun PauseView(viewModel: RecorderViewModel) {
    val elapsed by viewModel.elapsedMs.collectAsState()

    Text(text = formatElapsed(elapsed), fontSize = 32.sp)
    Spacer(modifier = Modifier.height(24.dp))
    Row {
        Button(onClick = { viewModel.unpauseRecording() }) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Resume recording"
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Button(onClick = { viewModel.acceptRecord() }) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Accept recording"
            )
        }
    }
}

@Composable
fun RecordingView(viewModel: RecorderViewModel) {
    val elapsed by viewModel.elapsedMs.collectAsState()

    Text(text = formatElapsed(elapsed), fontSize = 32.sp)
    Spacer(modifier = Modifier.height(24.dp))
    Row(verticalAlignment = Alignment.CenterVertically) {
        Button(onClick = { viewModel.pauseRecording() }) {
            Icon(
                imageVector = Icons.Default.Pause,
                contentDescription = "Pause recording"
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Button(onClick = { viewModel.rejectRecord() }) {
            Icon(
                imageVector = Icons.Default.Clear,
                contentDescription = "Reject recording"
            )
        }

    }
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
    Button(
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

private fun formatElapsed(ms: Long): String {
    val millis = (ms % 1000) / 10
    val totalSeconds = ms / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return if (hours > 0) {
        String.format("%d:%02d:%02d:%02d", hours, minutes, seconds, millis)
    } else {
        String.format("%02d:%02d:%02d", minutes, seconds, millis)
    }
}
