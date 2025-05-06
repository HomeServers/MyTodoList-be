package com.example.todolistapi.repository

import com.example.todolistapi.infrastructure.EventMessage
import org.springframework.data.jpa.repository.JpaRepository

interface EventMessageRepository: JpaRepository<EventMessage, Long> {
}