package com.melancholicbastard.handyasr.domain.node.usecases

import com.melancholicbastard.handyasr.domain.node.Node
import com.melancholicbastard.handyasr.domain.node.NodeRepository
import javax.inject.Inject

class GetNodeByIdUseCase @Inject constructor(
    private val nodeRepository: NodeRepository
) {
    suspend operator fun invoke(id: Long): Node? {
        return nodeRepository.getNodeById(id)
    }
}

