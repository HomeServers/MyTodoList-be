package com.example.todolistapi.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class Item(
    user: User,
    content: String,
    hash: String,
    status: ItemStatus,
    dueDate: LocalDateTime?
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user = user

    var content = content
        protected set

    val hash = hash

    @Enumerated(EnumType.STRING)
    var status = status
        protected set

    var dueDate = dueDate
        protected set

    fun update(content: String?, status: ItemStatus?, dueDate: LocalDateTime?) {
        updateContent(content)
        updateStatus(status)
        updateDueDate(dueDate)
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

    fun updateDueDate(dueDate: LocalDateTime?) {
        dueDate?.let {
            this.dueDate = dueDate
        }
    }

    fun expire() {
        this.status = ItemStatus.EXPIRED
    }
}

enum class ItemStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    EXPIRED,
}