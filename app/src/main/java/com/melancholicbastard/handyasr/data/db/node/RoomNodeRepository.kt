package com.melancholicbastard.handyasr.data.db.node

import com.melancholicbastard.handyasr.domain.node.Node
import com.melancholicbastard.handyasr.domain.node.NodeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomNodeRepository(
    private val nodeDao: NodeDao
) : NodeRepository {

    override suspend fun addNode(node: Node): Long {
        return nodeDao.insert(node.toEntity())
    }

    override suspend fun updateNode(node: Node) {
        nodeDao.update(node.toEntity())
    }

    override suspend fun deleteNodeById(id: Long) {
        nodeDao.deleteById(id)
    }

    override fun getNodesByDateDesc(): Flow<List<Node>> {
        return nodeDao.getNodesByDateDesc().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun searchNodesByTextOrTitle(query: String): Flow<List<Node>> {
        return nodeDao.searchByTextOrTitle(query.trim()).map { entities ->
            entities.map { it.toDomain() }
        }
    }
}

