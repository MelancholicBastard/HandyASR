package com.melancholicbastard.handyasr.domain.node.usecases

import com.melancholicbastard.handyasr.domain.node.Node
import com.melancholicbastard.handyasr.domain.node.NodeRepository
import kotlinx.coroutines.flow.Flow

class GetAllNodesUseCase(
    private val nodeRepository: NodeRepository
) {
    operator fun invoke(): Flow<List<Node>> {
        return nodeRepository.getAllNodes()
    }
}