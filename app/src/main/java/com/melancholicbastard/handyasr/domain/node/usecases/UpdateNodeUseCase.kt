package com.melancholicbastard.handyasr.domain.node.usecases

import com.melancholicbastard.handyasr.domain.node.Node
import com.melancholicbastard.handyasr.domain.node.NodeRepository

class UpdateNodeUseCase(
    private val nodeRepository: NodeRepository
) {
    suspend operator fun invoke(node: Node) {
        nodeRepository.updateNode(node)
    }
}

