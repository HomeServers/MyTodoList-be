package com.example.todolistapi.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class CorsConfig : WebMvcConfigurer {
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**") // 모든 경로에 대해
            .allowedOrigins("http://211.38.42.9:3000") // local
            .allowedOrigins("http://todo.nuhgnod.site") // http domain
            .allowedOrigins("https://todo.nuhgnod.site") // https domain
            .allowedHeaders("*") // 모든 헤더 허용
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 허용할 HTTP 메서드
            .allowCredentials(true) // 쿠키 사용 허용 여부
    }
}
