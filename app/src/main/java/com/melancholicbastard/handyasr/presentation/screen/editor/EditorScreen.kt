package com.melancholicbastard.handyasr.presentation.screen.editor

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.melancholicbastard.handyasr.presentation.ui.components.ProcessView
import com.melancholicbastard.handyasr.presentation.ui.components.MyLargeCircularButton
import com.melancholicbastard.handyasr.presentation.ui.components.PlayerBubble
import com.melancholicbastard.handyasr.presentation.viewmodel.EditViewModel
import com.melancholicbastard.handyasr.presentation.viewmodel.PlayerUiState
import com.melancholicbastard.handyasr.presentation.viewmodel.TextUiState
import java.util.concurrent.TimeUnit

@Composable
fun EditorScreen(
    viewModel: EditViewModel,
    bottomPadding: Dp = 0.dp,
    onBackClick: () -> Unit
) {
    val playerUiState by viewModel.playerUiState.collectAsState()
    val textUiState by viewModel.textUiState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(playerUiState.error) {
        playerUiState.error?.let {
            snackbarHostState.showSnackbar(
                message = it,
                actionLabel = "Закрыть",
                duration = SnackbarDuration.Short
            )
        }
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                )
            }
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .consumeWindowInsets(PaddingValues(bottom = bottomPadding))
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 16.dp)
            ) {
                val title by viewModel.title.collectAsState()
                OutlinedTextField(
                    value = title,
                    onValueChange = { viewModel.setTitle(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(text = "новая запись")
                    }
                )
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                border = BorderStroke(4.dp, MaterialTheme.colorScheme.primary),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    PlayerBubble(
                        viewModel,
                        onBackClick
                    )
                }
            }

            when (textUiState) {
                TextUiState.UndefinedTextState -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        MyLargeCircularButton(
                            onClick = { viewModel.requestText() },
                            icon = Icons.Default.Download,
                            contentDescription = "Get transcribed text"
                        )
                    }
                }

                TextUiState.ProcessTextState -> {
                    ProcessView()
                }

                TextUiState.DefinedTextState -> {
                    val text by viewModel.text.collectAsState()
                    OutlinedTextField(
                        value = text,
                        onValueChange = { viewModel.setText(it) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MySlider(
    playerUiState: PlayerUiState,
    seekTo: (Int) -> Unit,
    thumbRadius: Dp = 6.dp,
    trackHeight: Dp = 4.dp
) {
    val duration = playerUiState.durationMs.coerceAtLeast(0)
    val position =
        playerUiState.positionMs.coerceIn(0, if (duration == 0) Int.MAX_VALUE else duration)

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
                thumbSize = DpSize(width = thumbRadius * 2, height = thumbRadius * 2)
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