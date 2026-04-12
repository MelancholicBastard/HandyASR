package com.melancholicbastard.handyasr.presentation.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProcessView() {
    CircularProgressIndicator(modifier = Modifier.size(48.dp), strokeWidth = 4.dp)
    Spacer(modifier = Modifier.height(12.dp))
    Text(text = "Обработка...", style = MaterialTheme.typography.bodySmall)
}