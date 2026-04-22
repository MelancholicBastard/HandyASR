package com.melancholicbastard.handyasr.domain.node.usecases

import com.melancholicbastard.handyasr.domain.node.Node
import com.melancholicbastard.handyasr.domain.node.NodeRepository

class GetNodeByIdUseCase(
    private val nodeRepository: NodeRepository
) {
    suspend operator fun invoke(id: Long): Node? {
        return nodeRepository.getNodeById(id)
    }
}

