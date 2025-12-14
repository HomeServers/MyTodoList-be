package com.example.todolistapi.service

import com.example.todolistapi.controller.requests.auth.SigninRequest
import com.example.todolistapi.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val sessionService: SessionService,
) {
    fun signin(request: SigninRequest): String? {
        val user = userRepository.findByAccountAndPassword(request.account, request.password) ?: return null

        try {
            return sessionService.createToken(user.id)
        } catch (e: RuntimeException) {
            return sessionService.refreshToken(user.id)
        }
    }
}