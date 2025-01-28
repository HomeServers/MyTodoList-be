package com.example.todolistapi.entity

import jakarta.persistence.*

@Entity
class Item(
    var content: String,
    val hash: String,
    @Enumerated(EnumType.STRING)
    var status: ItemStatus
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    fun update(content: String?, status: ItemStatus?) {
        updateContent(content)
        updateStatus(status)
    }

    fun updateContent(content: String?) {
        content?.let {
            this.content = it
        }
    }

    fun updateStatus(status: ItemStatus?) {
        status?.let {
            this.status = status
        }
    }
}

enum class ItemStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED
}