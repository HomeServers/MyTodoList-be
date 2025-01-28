package com.example.todolistapi.controller

import com.example.todolistapi.controller.requests.item.ItemRequest
import com.example.todolistapi.entity.Item
import com.example.todolistapi.service.ItemService
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/items")
class ItemController(
    private val itemService: ItemService,
) {
    @GetMapping
    fun getItems(): ResponseEntity<List<Item>> {
        val response = itemService.getItems()
        return ResponseEntity(response, HttpStatus.OK)
    }

    @PostMapping
    fun createItem(
        @RequestBody request: ItemRequest
    ): ResponseEntity<Item> {
        val response = itemService.createItem(request)
        return ResponseEntity(response, CREATED)
    }

    @PutMapping("/{itemId}")
    fun updateItem(
        @PathVariable("itemId") itemId: Long,
        @RequestBody request: ItemRequest
    ) {
        itemService.updateItem(itemId, request)
    }
}