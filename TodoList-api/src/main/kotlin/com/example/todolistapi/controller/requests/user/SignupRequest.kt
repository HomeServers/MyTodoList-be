package com.example.todolistapi.controller.requests.user

data class SignupRequest(
    val account: String,
    val password: String,
    val name: String
)
