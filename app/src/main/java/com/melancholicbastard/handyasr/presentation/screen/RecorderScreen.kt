package com.melancholicbastard.handyasr.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.remember

@Composable
fun RecorderScreen() {
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