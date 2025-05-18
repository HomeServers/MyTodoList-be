package com.example.todolistapi.dto

import com.example.todolistapi.entity.Session
import java.time.Instant

data class SessionDto(
    val id: Long,
    val userId: Long,
    val exp: Instant
) {
    companion object {
        fun from(session: Session) : SessionDto {
            return SessionDto(
                id = session.id,
                userId = session.user.id,
                exp = session.exp
            )
        }
    }
}