package com.example.todolistapi.service

import com.example.todolistapi.entity.Session
import com.example.todolistapi.repository.SessionRepository
import com.example.todolistapi.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.*

@Service
class SessionService(
    private val userRepository: UserRepository,
    private val sessionRepository: SessionRepository
) {

    @Transactional
    fun createToken(userId: Long) : String {
        val user = userRepository.findById(userId).get()
        val session = Session(
            user = user,
            accessToken = UUID.randomUUID().toString(),
            exp = Instant.now().plusSeconds(60 * 30) // 30 min
        )

        sessionRepository.save(session)
        return session.accessToken
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun refreshToken(userId: Long): String {
        val session = sessionRepository.findByUserId(userId)!!

        val accessToken = session.refresh()
        return accessToken
    }
}