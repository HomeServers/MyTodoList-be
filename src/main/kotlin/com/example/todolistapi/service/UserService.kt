package com.example.todolistapi.service

import com.example.todolistapi.controller.requests.user.SignupRequest
import com.example.todolistapi.entity.User
import com.example.todolistapi.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.dao.DuplicateKeyException
import org.springframework.stereotype.Service
import kotlin.RuntimeException

@Service
class UserService(
    private val userRepository: UserRepository
) {
    @Throws(RuntimeException::class)
    @Transactional
    fun signup(request: SignupRequest): Long {
        val user = User(
            account = request.account,
            name = request.name,
            password = request.password
        )
        val persistedUser  = try {
            userRepository.save(user)
        } catch (e: RuntimeException) { // duplicate account
            throw DuplicateKeyException("ID가 중복되었습니다", e)
        }

        return persistedUser.id
    }
}