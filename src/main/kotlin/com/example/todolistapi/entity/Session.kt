package com.example.todolistapi.entity

import jakarta.persistence.*
import java.time.Instant
import java.util.UUID

@Entity
@Table
class Session(
    user: User,
    accessToken: String,
    exp: Instant,
) : BaseEntity(){
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L
        protected set

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user = user

    var accessToken = accessToken

    var exp = exp

    fun refresh(): String {
        this.accessToken = UUID.randomUUID().toString()
        this.exp = Instant.now().plusSeconds(60 * 30)
        return accessToken
    }
}