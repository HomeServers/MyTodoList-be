package com.example.todolistapi.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant

@EntityListeners(AuditingEntityListener::class)
@MappedSuperclass
class BaseEntity() {
    @CreatedDate
    var createdAt: Instant = Instant.now()
        protected set

    @LastModifiedDate
    var updatedAt: Instant = Instant.now()
        protected set
}