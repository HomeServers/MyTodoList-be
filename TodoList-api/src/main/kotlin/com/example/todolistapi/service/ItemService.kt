package com.example.todolistapi.service

import com.example.todolistapi.controller.requests.item.ItemRequest
import com.example.todolistapi.dto.ItemDto
import com.example.todolistapi.entity.Item
import com.example.todolistapi.entity.ItemStatus
import com.example.todolistapi.repository.ItemRepository
import com.example.todolistapi.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ItemService(
    private val itemRepository: ItemRepository,
    private val sessionService: SessionService,
    private val userRepository: UserRepository,
) {
    fun getItems(principal: String): List<ItemDto> {
        val session = sessionService.getSession(principal)
        val userId = session.userId
        return itemRepository.findAllByUserId(userId).map {
            ItemDto.from(it)
        }
    }

    fun getItems(status: List<ItemStatus>): List<ItemDto> {
        return itemRepository.findAllByStatusIn(status).map {
            ItemDto.from(it)
        }
    }

    @Transactional
    fun createItem(principal: String, request: ItemRequest): ItemDto {
        val session = sessionService.getSession(principal)
        val userId = session.userId
        val author = userRepository.findById(userId).orElseThrow { NotFoundException() }

        val item = Item(
            content = request.content ?: "",
            hash = request.hash ?: UUID.randomUUID().toString(),
            status = request.status ?: ItemStatus.PENDING,
            dueDate = request.dueDate,
            user = author
        )
        val persistedItem = itemRepository.save(item)
        return ItemDto.from(persistedItem)
    }

    @Transactional
    fun updateItem(principal: String, itemId: Long, request: ItemRequest) {
        val session = sessionService.getSession(principal)
        val userId = session.userId

        val item = itemRepository.findByIdAndUserId(itemId, userId) ?: throw NotFoundException()
        item.update(request.content, request.status, request.dueDate)
    }

    @Transactional
    fun expireItem(itemId: Long) {
        val item = itemRepository.findById(itemId).orElseThrow { NotFoundException() }
        item.expire()
    }

    @Transactional
    fun removeItem(principal: String, itemId: Long): Long {
        val session = sessionService.getSession(principal)
        val userId = session.userId

        val item = itemRepository.findByIdAndUserId(itemId, userId) ?: throw NotFoundException()
        itemRepository.delete(item)
        return item.id
    }
}