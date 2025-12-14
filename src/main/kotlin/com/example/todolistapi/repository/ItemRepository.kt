package com.example.todolistapi.repository

import com.example.todolistapi.entity.Item
import com.example.todolistapi.entity.ItemStatus
import org.springframework.data.jpa.repository.JpaRepository

interface ItemRepository: JpaRepository<Item, Long> {

    fun findAllByStatusIn(status: List<ItemStatus>) : List<Item>
    fun findAllByUserId(userId: Long): List<Item>
    fun findByIdAndUserId(itemId: Long, userId: Long): Item?
}