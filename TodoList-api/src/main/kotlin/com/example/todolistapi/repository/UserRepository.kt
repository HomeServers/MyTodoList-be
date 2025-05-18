package com.example.todolistapi.repository

import com.example.todolistapi.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository: JpaRepository<User, Long> {
    fun findByAccountAndPassword(account: String, password: String) : User?
}