package com.example.todolistapi.dto

import com.example.todolistapi.entity.Item
import com.example.todolistapi.entity.ItemStatus
import java.time.Instant

data class ItemDto(
    val id: Long,
    val content: String,
    val hash: String,
    val status: ItemStatus,
    val expiredAt: Instant?
) {
    companion object {
        fun from(item: Item): ItemDto {
            return ItemDto(
                id = item.id,
                content = item.content,
                hash = item.hash,
                status = item.status,
                expiredAt = item.expiredAt
            )
        }
    }
}