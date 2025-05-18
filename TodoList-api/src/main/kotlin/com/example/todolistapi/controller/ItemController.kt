package com.example.todolistapi.controller

import com.example.todolistapi.controller.requests.item.ItemRequest
import com.example.todolistapi.dto.ItemDto
import com.example.todolistapi.entity.Item
import com.example.todolistapi.service.ItemService
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/items")
class ItemController(
    private val itemService: ItemService,
) {
    @GetMapping
    fun getItems(
        @AuthenticationPrincipal principal: String
    ): ResponseEntity<List<ItemDto>> {
        val response = itemService.getItems(principal)
        return ResponseEntity(response, HttpStatus.OK)
    }

    @PostMapping
    fun createItem(
        @AuthenticationPrincipal principal: String,
        @RequestBody request: ItemRequest
    ): ResponseEntity<Item> {
        val response = itemService.createItem(principal, request)
        return ResponseEntity(response, CREATED)
    }

    @PutMapping("/{itemId}")
    fun updateItem(
        @AuthenticationPrincipal principal: String,
        @PathVariable("itemId") itemId: Long,
        @RequestBody request: ItemRequest
    ) {
        itemService.updateItem(principal, itemId, request)
    }
}