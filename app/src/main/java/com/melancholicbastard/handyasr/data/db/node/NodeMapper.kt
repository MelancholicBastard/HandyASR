package com.melancholicbastard.handyasr.data.db.node

import com.melancholicbastard.handyasr.domain.node.Node

fun NodeEntity.toDomain(): Node {
    return Node(
        id = id,
        title = title,
        text = text,
        createdAt = createdAt,
        audioFileName = audioFileName
    )
}

fun Node.toEntity(): NodeEntity {
    return NodeEntity(
        id = id,
        title = title,
        text = text,
        createdAt = createdAt,
        audioFileName = audioFileName
    )
}

