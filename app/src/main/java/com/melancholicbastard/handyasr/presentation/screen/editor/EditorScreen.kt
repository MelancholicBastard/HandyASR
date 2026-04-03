package com.melancholicbastard.handyasr.presentation.screen.editor

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.melancholicbastard.handyasr.presentation.viewmodel.EditViewModel
import com.melancholicbastard.handyasr.presentation.viewmodel.PlayerUiState
import java.util.concurrent.TimeUnit

@Composable
fun EditorScreen(
    viewModel: EditViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if (uiState.isLoading) {
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        } else {
            MySlider(
                uiState,
                { to ->
                    viewModel.seekTo(to)
                }
            )

            Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                IconButton(onClick = { viewModel.skipBackward() }) {
                    Icon(
                        imageVector = Icons.Default.ChevronLeft,
                        contentDescription = "Skip backward"
                    )
                }
                IconButton(onClick = { viewModel.togglePlayPause() }) {
                    if (uiState.isPlaying) {
                        Icon(
                            imageVector = Icons.Default.Pause,
                            contentDescription = "Pause"
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play"
                        )
                    }
                }
                IconButton(onClick = { viewModel.skipForward() }) {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Skip backward"
                    )
                }
            }
        }

        uiState.error?.let { err ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Ошибка: $err", color = MaterialTheme.colorScheme.error)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MySlider(
    uiState: PlayerUiState,
    seekTo: (Int) -> Unit,
    thumbRadius: Dp = 6.dp,
    trackHeight: Dp = 4.dp
) {
    val duration = uiState.durationMs.coerceAtLeast(0)
    val position = uiState.positionMs.coerceIn(0, if (duration == 0) Int.MAX_VALUE else duration)

    val interactionSource = remember { MutableInteractionSource() }

    Text(text = formatMs(position) + " / " + formatMs(duration))

    Slider(
        value = if (duration == 0) 0f else position.toFloat() / duration.coerceAtLeast(1),
        onValueChange = { frac ->
            if (duration > 0) {
                val to = (frac * duration).toInt()
                seekTo(to)
            }
        },
        interactionSource = interactionSource,
        modifier = Modifier.fillMaxWidth(),
        thumb = {
            SliderDefaults.Thumb(
                interactionSource = interactionSource,
                thumbSize = DpSize( width = thumbRadius * 2, height = thumbRadius * 2 )
            )
        },
        track = { sliderState ->
            SliderDefaults.Track(
                sliderState = sliderState,
                modifier = Modifier.height(trackHeight)
            )
        }
    )
}

private fun formatMs(ms: Int): String {
    if (ms <= 0) return "0:00"
    val minutes = TimeUnit.MILLISECONDS.toMinutes(ms.toLong())
    val seconds = TimeUnit.MILLISECONDS.toSeconds(ms.toLong()) - TimeUnit.MINUTES.toSeconds(minutes)
    return String.format("%d:%02d", minutes, seconds)
}