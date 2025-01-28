package com.example.todolistapi.repository

import com.example.todolistapi.entity.Item
import org.springframework.data.jpa.repository.JpaRepository

interface ItemRepository: JpaRepository<Item, Long> {
}