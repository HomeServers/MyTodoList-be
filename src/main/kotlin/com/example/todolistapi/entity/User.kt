package com.example.todolistapi.entity

import jakarta.persistence.*

@Entity
@Table(
    name = "users",
    uniqueConstraints = [UniqueConstraint(columnNames = ["account"])]
)
class User(
    account: String,
    name: String,
    password: String,
) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L
        protected set

    val account = account

    var name = name
        protected set

    var password = password
}