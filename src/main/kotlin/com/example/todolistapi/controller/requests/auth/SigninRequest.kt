package com.example.todolistapi.controller.requests.auth

data class SigninRequest(
    val account: String,
    val password: String
)
