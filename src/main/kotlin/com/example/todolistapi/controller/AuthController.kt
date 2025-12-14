package com.example.todolistapi.controller

import com.example.todolistapi.controller.requests.auth.SigninRequest
import com.example.todolistapi.controller.requests.user.SignupRequest
import com.example.todolistapi.controller.response.ErrorResponse
import com.example.todolistapi.controller.response.Response
import com.example.todolistapi.controller.response.SuccessResponse
import com.example.todolistapi.service.AuthService
import com.example.todolistapi.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

@RestController
@RequestMapping("/api/auths")
class AuthController(
    private val userService: UserService,
    private val authService: AuthService
) {
    @PostMapping("/signup")
    fun signup(
        @RequestBody request: SignupRequest
    ): ResponseEntity<Response<*>> {
        try {
            val userId = userService.signup(request)
            return ResponseEntity(SuccessResponse(data = userId), HttpStatus.OK)
        } catch (e: RuntimeException) {
            val response = ErrorResponse(
                data = 0,
                code = "DUPLICATE_USER",
                status = HttpStatus.CONFLICT.value(),
                message = e.message,
                time = Instant.now()
            )
            return ResponseEntity(response, HttpStatus.CONFLICT)
        }
    }

    @PostMapping("/signin")
    fun signin(
        @RequestBody request: SigninRequest
    ): ResponseEntity<Response<*>> {
        val accessToken = authService.signin(request)
        if(accessToken == null) {
            val response = ErrorResponse(
                data = 0,
                code = "UN_AUTHORIZED",
                status = HttpStatus.UNAUTHORIZED.value(),
                message = "인증에 실패했습니다",
                time = Instant.now()
            )
            return ResponseEntity(response, HttpStatus.UNAUTHORIZED)
        }

        return ResponseEntity(SuccessResponse(data = accessToken), HttpStatus.OK)
    }
}