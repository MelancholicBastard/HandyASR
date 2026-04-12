package com.melancholicbastard.handyasr.domain.node.usecases

import com.melancholicbastard.handyasr.domain.node.NodeRepository

class DeleteNodeByIdUseCase(
    private val nodeRepository: NodeRepository
) {
    suspend operator fun invoke(id: Long) {
        nodeRepository.deleteNodeById(id)
    }
}

