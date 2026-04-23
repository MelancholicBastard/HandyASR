package com.melancholicbastard.handyasr.domain.node

import kotlinx.coroutines.flow.Flow

interface NodeRepository {
    suspend fun addNode(node: Node): Long
    suspend fun updateNode(node: Node)
    suspend fun deleteNodeById(id: Long)
    suspend fun deleteAllNodes()
    suspend fun getNodeById(id: Long): Node?
    fun searchNodesBy(
        startTimestamp: Long?,
        endTimestamp: Long?,
        query: String?
    ): Flow<List<Node>>
    fun getAllNodes(): Flow<List<Node>>
}
