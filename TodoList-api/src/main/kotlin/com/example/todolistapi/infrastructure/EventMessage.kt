package com.example.todolistapi.infrastructure

import com.example.todolistapi.entity.BaseEntity
import jakarta.persistence.*
import java.time.Instant

@Entity
@Table
class EventMessage(
    content: String,
    eventHash: String,
    eventTime: Instant,
    status: EventMessageStatus
) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L

    val content = content

    val eventHash = eventHash

    val eventTime = eventTime

    @Enumerated(value = EnumType.STRING)
    var status = status
}

enum class EventMessageStatus {
    FAIL,
    SUCCESS
}