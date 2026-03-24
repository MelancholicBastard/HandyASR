package com.melancholicbastard.handyasr.presentation.screen.recorder

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.melancholicbastard.handyasr.presentation.viewmodel.RecorderViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.melancholicbastard.handyasr.presentation.viewmodel.RecordScreenUIState

@Composable
fun RecorderScreen(viewModel: RecorderViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    when (uiState) {
        RecordScreenUIState.IdleUIState -> IdleView()
        RecordScreenUIState.StartUIState -> RecordingView()
        RecordScreenUIState.PauseUIState -> PauseView()
        RecordScreenUIState.ProcessUIState -> ProcessView()
        RecordScreenUIState.RedactUIState -> RedactView()
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val counterState = remember { mutableIntStateOf(0) }
        Text(text = "Экран конструктора")
        Button(
            onClick = {
                counterState.intValue += 1
            }
        ) {
            Text(text = "${counterState.intValue}")
        }
    }
}

@Composable
fun RedactView() {
    TODO("Not yet implemented")
}

@Composable
fun ProcessView() {
    TODO("Not yet implemented")
}

@Composable
fun PauseView() {
    TODO("Not yet implemented")
}

@Composable
fun RecordingView() {
    TODO("Not yet implemented")
}

@Composable
fun IdleView() {
    TODO("Not yet implemented")
}