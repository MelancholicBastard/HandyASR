package com.melancholicbastard.handyasr.domain.node.usecases

import com.melancholicbastard.handyasr.domain.node.Node
import com.melancholicbastard.handyasr.domain.node.NodeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchNodesByUseCase @Inject constructor(
    private val repository: NodeRepository
) {
    operator fun invoke(
        startTimestamp: Long?,
        endTimestamp: Long?,
        query: String?
    ): Flow<List<Node>> {
        return repository.searchNodesBy(startTimestamp, endTimestamp, query)
    }
}

