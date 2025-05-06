package com.example.todolistapi.service

import com.example.todolistapi.controller.requests.item.ItemRequest
import com.example.todolistapi.dto.ItemDto
import com.example.todolistapi.entity.Item
import com.example.todolistapi.entity.ItemStatus
import com.example.todolistapi.repository.ItemRepository
import jakarta.transaction.Transactional
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ItemService(
    private val itemRepository: ItemRepository,
) {
    fun getItems(): List<ItemDto> {
        return itemRepository.findAll().map {
            ItemDto.from(it)
        }
    }

    fun getItems(status: List<ItemStatus>): List<ItemDto> {
        return itemRepository.findAllByStatusIn(status).map {
            ItemDto.from(it)
        }
    }

    @Transactional
    fun createItem(request: ItemRequest): Item {
        val item = Item(
            content = request.content ?: "",
            hash = request.hash ?: UUID.randomUUID().toString(),
            status = request.status ?: ItemStatus.PENDING,
            dueDate = request.dueDate
        )
        return itemRepository.save(item)
    }

    @Transactional
    fun updateItem(itemId: Long, request: ItemRequest) {
        val item = itemRepository.findById(itemId).orElseThrow { NotFoundException() }
        item.update(request.content, request.status, request.dueDate)
    }

    @Transactional
    fun expireItem(itemId: Long) {
        println("ItemService.expireItem")
        val item = itemRepository.findById(itemId).orElseThrow { NotFoundException() }
        item.expire()
    }
}