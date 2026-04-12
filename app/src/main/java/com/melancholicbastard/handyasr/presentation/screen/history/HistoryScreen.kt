package com.melancholicbastard.handyasr.presentation.screen.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.melancholicbastard.handyasr.presentation.viewmodel.HistoryViewModel

@Composable
fun HistoryScreen( viewModel: HistoryViewModel ) {
    val counterState by viewModel.integer.collectAsState()
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = "Экран записей")
        Button(
            onClick = {
                viewModel.incr()
            }
        ) {
            Text(text = "${counterState}")
        }
        Button(
            onClick = {
                viewModel.onOpenEditorForExistingRecord("$counterState")
            }
        ) {
            Text(text = "${counterState}")
        }
    }
}