package com.melancholicbastard.handyasr.presentation.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardReturn
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Forward5
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay5
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.melancholicbastard.handyasr.presentation.screen.editor.MySlider
import com.melancholicbastard.handyasr.presentation.viewmodel.EditViewModel
import com.melancholicbastard.handyasr.presentation.viewmodel.PlayerUiState

@Composable
fun PlayerBubble(
    viewModel: EditViewModel
) {
    var isPlayerExpanded by remember { mutableStateOf(false) }
    val playerUiState by viewModel.playerUiState.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()

    if (playerUiState.isLoading) {
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
    } else {

        AnimatedVisibility(
            visible = isPlayerExpanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            PlayerContent(
                playerUiState = playerUiState,
                seekTo = { ms ->
                    viewModel.seekTo(ms)
                },
                skipBackward = { viewModel.skipBackward() },
                skipForward = { viewModel.skipForward() },
                togglePlayPause = { viewModel.togglePlayPause() }
            )
        }

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            AnimatedVisibility(
                visible = !isPlayerExpanded,
                enter = fadeIn() + expandHorizontally(),
                exit = fadeOut() + shrinkHorizontally()
            ) {
                Button(
                    onClick = { viewModel.onBackButtonPressed() },
                    modifier = Modifier.weight(0.5f)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardReturn,
                        contentDescription = "Exit editor"
                    )

                }
            }

            Spacer(modifier = Modifier.weight(0.05f))
            Button(
                onClick = { isPlayerExpanded = !isPlayerExpanded },
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Развернуть плеер")
            }
            Spacer(modifier = Modifier.weight(0.05f))

            AnimatedVisibility(
                visible = !isPlayerExpanded,
                enter = fadeIn() + expandHorizontally(),
                exit = fadeOut() + shrinkHorizontally()
            ) {
                Button(
                    onClick = {
                        viewModel.saveNode()
                    },
                    enabled = !isSaving,
                    modifier = Modifier.weight(0.5f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = "Save node"
                    )
                }
            }
        }
    }
}

@Composable
fun PlayerContent(
    playerUiState: PlayerUiState,
    seekTo: (Int) -> Unit,
    skipBackward: () -> Unit,
    skipForward: () -> Unit,
    togglePlayPause: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MySlider(
            playerUiState,
            seekTo
        )

        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = { skipBackward() }) {
                Icon(
                    imageVector = Icons.Default.Replay5,
                    contentDescription = "Skip backward"
                )
            }
            IconButton(onClick = { togglePlayPause() }) {
                if (playerUiState.isPlaying) {
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
            IconButton(onClick = { skipForward() }) {
                Icon(
                    imageVector = Icons.Default.Forward5,
                    contentDescription = "Skip forward"
                )
            }
        }
    }
}