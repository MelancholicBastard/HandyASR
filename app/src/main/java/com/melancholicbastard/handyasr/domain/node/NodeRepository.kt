package com.melancholicbastard.handyasr.domain.node

import kotlinx.coroutines.flow.Flow

interface NodeRepository {
    suspend fun addNode(node: Node): Long
    suspend fun updateNode(node: Node)
    suspend fun deleteNodeById(id: Long)
    fun getNodesByDateDesc(): Flow<List<Node>>
    fun searchNodesByTextOrTitle(query: String): Flow<List<Node>>
}
