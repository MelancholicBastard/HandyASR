package com.melancholicbastard.handyasr.presentation.screen.editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.melancholicbastard.handyasr.presentation.viewmodel.EditViewModel

@Composable
fun EditorScreen(
    viewModel: EditViewModel,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val counterState by viewModel.integer.collectAsState()
        Text(text = if (viewModel.isNewRecord) "Новая запись" else "Редактирование записи")
        Button(
            onClick = {
                viewModel.decr()
            }
        ) {
            Text(text = "${counterState}")
        }
        Button(onClick = onBackClick) {
            Text(text = "Назад")
        }
    }
}