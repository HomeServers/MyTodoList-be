package com.example.todolistapi.entity

import jakarta.persistence.*
import java.time.Instant

@Entity
class Item(
    content: String,
    hash: String,
    status: ItemStatus,
    expiredAt: Instant?
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    var content = content
        protected set

    val hash = hash

    @Enumerated(EnumType.STRING)
    var status = status
        protected set

    var expiredAt = expiredAt
        protected set

    fun update(content: String?, status: ItemStatus?, expiredAt: Instant?) {
        updateContent(content)
        updateStatus(status)
        updateExpiredAt(expiredAt)
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

    fun updateExpiredAt(expiredAt: Instant?) {
        expiredAt?.let {
            this.expiredAt = expiredAt
        }
    }
}

enum class ItemStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED
}