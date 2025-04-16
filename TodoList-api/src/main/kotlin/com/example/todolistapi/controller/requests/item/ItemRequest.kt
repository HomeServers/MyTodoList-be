package com.example.todolistapi.controller.requests.item

import com.example.todolistapi.entity.ItemStatus
import java.time.Instant

data class ItemRequest(
    val id: Long?,
    val hash: String?,
    val content: String?,
    val status: ItemStatus? = ItemStatus.PENDING,
    val expiredAt: Instant? = null,
)