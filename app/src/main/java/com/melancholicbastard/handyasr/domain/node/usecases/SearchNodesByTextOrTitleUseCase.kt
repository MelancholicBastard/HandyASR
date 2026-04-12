package com.melancholicbastard.handyasr.domain.node.usecases

import com.melancholicbastard.handyasr.domain.node.Node
import com.melancholicbastard.handyasr.domain.node.NodeRepository
import kotlinx.coroutines.flow.Flow

class SearchNodesByTextOrTitleUseCase(
    private val nodeRepository: NodeRepository
) {
    operator fun invoke(query: String): Flow<List<Node>> {
        return nodeRepository.searchNodesByTextOrTitle(query)
    }
}

