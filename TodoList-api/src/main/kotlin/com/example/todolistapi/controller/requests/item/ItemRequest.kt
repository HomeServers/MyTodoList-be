package com.example.todolistapi.controller.requests.item

import com.example.todolistapi.entity.ItemStatus
import java.time.LocalDateTime

data class ItemRequest(
    val id: Long?,
    val hash: String?,
    val content: String?,
    val status: ItemStatus? = ItemStatus.PENDING,
    val dueDate: LocalDateTime? = null,
)