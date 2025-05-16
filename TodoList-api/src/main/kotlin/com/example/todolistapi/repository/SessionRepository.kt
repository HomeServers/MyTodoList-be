package com.example.todolistapi.repository

import com.example.todolistapi.entity.Session
import org.springframework.data.jpa.repository.JpaRepository

interface SessionRepository: JpaRepository<Session, Long> {
    fun findByUserId(id: Long) : Session?
}