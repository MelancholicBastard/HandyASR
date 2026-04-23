package com.melancholicbastard.handyasr.domain.node

data class Node(
    val id: Long = 0,
    val title: String,
    val text: String?,
    val createdAt: Long,
    val audioFileName: String
)

