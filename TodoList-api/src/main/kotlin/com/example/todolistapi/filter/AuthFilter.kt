package com.example.todolistapi.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class AuthFilter : OncePerRequestFilter() {
    private val logger = LoggerFactory.getLogger(this::class.java)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authValue = request.getHeader("Authorization")
        if (authValue != null) {
            val accessToken = authValue.split(" ")[1]
            MDC.put("auth", accessToken)
            val authToken = UsernamePasswordAuthenticationToken(accessToken, accessToken)
            SecurityContextHolder.getContext().authentication = authToken
        }

        filterChain.doFilter(request, response)
    }
}