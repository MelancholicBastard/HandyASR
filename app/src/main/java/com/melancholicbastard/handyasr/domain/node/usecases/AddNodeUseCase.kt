package com.melancholicbastard.handyasr.domain.node.usecases

import com.melancholicbastard.handyasr.domain.node.Node
import com.melancholicbastard.handyasr.domain.node.NodeRepository
import javax.inject.Inject

class AddNodeUseCase @Inject constructor(
    private val nodeRepository: NodeRepository
) {
    suspend operator fun invoke(node: Node): Long {
        return nodeRepository.addNode(node)
    }
}

