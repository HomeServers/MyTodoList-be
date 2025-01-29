package com.example.todolistapi.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class CorsConfig : WebMvcConfigurer {
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**") // 모든 경로에 대해
            .allowedOriginPatterns("http://localhost:3000") // 특정 출처 지정
//            .allowedOriginPatterns("*")
//            .allowedOrigins("*") // 허용할 출처
            .allowedHeaders("*") // 모든 헤더 허용
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 허용할 HTTP 메서드
            .allowCredentials(true) // 쿠키 사용 허용 여부
    }
}
