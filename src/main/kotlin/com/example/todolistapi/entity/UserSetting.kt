package com.example.todolistapi.entity

import jakarta.persistence.*

@Entity
@Table
class UserSetting(
    user: User,
    loginSetting: LoginSetting
) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    val user = user

    @Enumerated(EnumType.STRING)
    var loginSetting = loginSetting
}

enum class LoginSetting {
    PASSWORD,
    KAKAO,
    GOOGLE,
    GITHUB
}