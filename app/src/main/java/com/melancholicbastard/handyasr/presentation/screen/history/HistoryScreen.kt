package com.melancholicbastard.handyasr.presentation.screen.history

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.google.android.material.datepicker.MaterialDatePicker
import com.melancholicbastard.handyasr.presentation.ui.components.MyLargeCircularButton
import com.melancholicbastard.handyasr.presentation.viewmodel.HistoryViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(viewModel: HistoryViewModel) {
    val nodes by viewModel.nodes.collectAsState()
    val query by viewModel.searchQuery.collectAsState()
    val selectedDate by viewModel.selectedDateMillis.collectAsState()
    val isLoading by viewModel.isLoadingNodes.collectAsState()
    var showDatePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {


        Surface(
            shape = RoundedCornerShape(6.dp),
            color = MaterialTheme.colorScheme.onPrimary,
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
        ) {
            Column {
                OutlinedTextField(
                    value = query,
                    onValueChange = viewModel::setSearchQuery,
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search by text or title",
                        )
                    },
                    placeholder = { Text("Введите текст или заголовок") },
                    singleLine = true
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.weight(0.05f))
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                MaterialTheme.colorScheme.onPrimary,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { showDatePicker = true },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = selectedDate?.let { "Дата: ${formatDate(it)}" }
                                ?: "Выбрать дату")

                        IconButton(
                            onClick = { showDatePicker = true },
                        ) {
                            Icon(
                                imageVector = Icons.Filled.DateRange,
                                contentDescription = "Выбрать дату"
                            )
                        }
                    }

                    Spacer(modifier = Modifier.weight(0.05f))

                    MyLargeCircularButton(
                        onClick = {
//                            viewModel.clearAllRecords()
                            viewModel.setSelectedDate(null)
                        },
                        icon = Icons.Default.Replay,
                        contentDescription = "Set null date",
                        diameter = 36.dp,
                    )

                    Spacer(modifier = Modifier.weight(0.05f))
                }
            }
        }

        if (isLoading) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items = nodes, key = { it.id }) { node ->

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.onSecondary,
                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.secondary),
                    modifier = Modifier.clickable { viewModel.openNode(node) }
                ) {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(IntrinsicSize.Min)
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = node.title,
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    text = node.text ?: "",
                                    maxLines = 4,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Column(
                                modifier = Modifier
                                    .weight(0.1f)
                                    .fillMaxHeight(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                MyLargeCircularButton(
                                    onClick = { viewModel.deleteNodeById(node.id) },
                                    icon = Icons.Default.Delete,
                                    contentDescription = "Delete one node",
                                    diameter = 36.dp,
                                    containerColor = MaterialTheme.colorScheme.secondary
                                )
                                Spacer(
                                    modifier = Modifier
                                        .size(8.dp)
                                )
                                Text(
                                    text = formatTime(node.createdAt),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                }

            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.setSelectedDate(datePickerState.selectedDateMillis)
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Отмена")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

private fun formatDate(millis: Long): String {
    return SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date(millis))
}

private fun formatTime(millis: Long): String {
    return SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(millis))
}
