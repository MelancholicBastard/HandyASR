package com.melancholicbastard.handyasr.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.melancholicbastard.handyasr.domain.node.Node
import com.melancholicbastard.handyasr.domain.node.usecases.DeleteAllNodesUseCase
import com.melancholicbastard.handyasr.domain.node.usecases.DeleteNodeByIdUseCase
import com.melancholicbastard.handyasr.domain.node.usecases.GetAllNodesUseCase
import com.melancholicbastard.handyasr.domain.node.usecases.SearchNodesByUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import java.util.TimeZone
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val searchNodesByUseCase: SearchNodesByUseCase,
    private val deleteNodeByIdUseCase: DeleteNodeByIdUseCase,
    private val deleteAllNodesUseCase: DeleteAllNodesUseCase,
    private val getAllNodesUseCase: GetAllNodesUseCase
) : ViewModel() {

    private val _navigationEvents = MutableSharedFlow<HistoryNavigationEvent>(replay = 0)
    val navigationEvents: SharedFlow<HistoryNavigationEvent> = _navigationEvents

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private fun nodeSourceFlow() = combine(
        _searchQuery.debounce(300),
        _selectedDateMillis
    ) { query, dateMillis ->
        query to dateMillis
    }.flatMapLatest { (query, dateMillis) ->
        _isLoadingNodes.value = true

        try {
            if (dateMillis == null && query.isBlank()) {
                getAllNodesUseCase()
            } else {
                val (start, end) = dateMillis?.let {
                    getTimestampsForDate(Date(it))
                } ?: (null to null)
                searchNodesByUseCase(start, end, query.trim())
            }
        } finally {
            _isLoadingNodes.value = false
        }
    }

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedDateMillis = MutableStateFlow<Long?>(null)
    val selectedDateMillis: StateFlow<Long?> = _selectedDateMillis.asStateFlow()

    private val _isLoadingNodes = MutableStateFlow(false)
    val isLoadingNodes: StateFlow<Boolean> = _isLoadingNodes.asStateFlow()

    val nodes: StateFlow<List<Node>> = nodeSourceFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedDate(dateMillis: Long?) {
        _selectedDateMillis.value = dateMillis
    }

    fun deleteNodeById(id: Long) {
        viewModelScope.launch {
            deleteNodeByIdUseCase(id)
        }
    }

    fun openNode(node: Node) {
        viewModelScope.launch {
            _navigationEvents.emit(HistoryNavigationEvent.OpenEditorForExistingRecord(node.id.toString()))
        }
    }

    fun clearAllRecords() {
        viewModelScope.launch {
            deleteAllNodesUseCase()
        }
    }

    private fun getTimestampsForDate(date: Date): Pair<Long, Long> {
        val calendar = Calendar.getInstance(TimeZone.getDefault())
        calendar.time = date

        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        val start = calendar.timeInMillis

        calendar.add(Calendar.DAY_OF_MONTH, 1)
        val end = calendar.timeInMillis

        return start to end
    }
}

sealed class HistoryNavigationEvent {
    data class OpenEditorForExistingRecord(val recordId: String) : HistoryNavigationEvent()
}
