package com.example.todolistapi.controller

import com.example.todolistapi.controller.requests.item.ItemRequest
import com.example.todolistapi.controller.response.SuccessResponse
import com.example.todolistapi.dto.ItemDto
import com.example.todolistapi.service.ICalendarService
import com.example.todolistapi.service.ItemService
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/items")
class ItemController(
    private val itemService: ItemService,
    private val iCalendarService: ICalendarService,
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
    ): ResponseEntity<SuccessResponse<ItemDto>> {
        val item = itemService.createItem(principal, request)
        return ResponseEntity(SuccessResponse(data = item), HttpStatus.OK)
    }

    @PutMapping("/{itemId}")
    fun updateItem(
        @AuthenticationPrincipal principal: String,
        @PathVariable("itemId") itemId: Long,
        @RequestBody request: ItemRequest
    ) {
        itemService.updateItem(principal, itemId, request)
    }

    @DeleteMapping("/{itemId}")
    fun deleteItem(
        @AuthenticationPrincipal principal: String,
        @PathVariable("itemId") itemId: Long
    ): ResponseEntity<SuccessResponse<Long>> {
        val id = itemService.removeItem(principal, itemId)
        return ResponseEntity(SuccessResponse(data = id), HttpStatus.OK)
    }

    /**
     * 모든 할일을 .ics 파일로 export
     */
    @GetMapping("/export.ics")
    fun exportAllItemsToIcs(
        @AuthenticationPrincipal principal: String
    ): ResponseEntity<String> {
        val items = itemService.getItemEntities(principal)
        val icsContent = iCalendarService.exportToIcs(items)

        val headers = HttpHeaders()
        headers.contentType = MediaType.parseMediaType("text/calendar")
        headers.setContentDispositionFormData("attachment", "mytodolist.ics")

        return ResponseEntity(icsContent, headers, HttpStatus.OK)
    }

    /**
     * .ics 파일을 import하여 할일 생성
     */
    @PostMapping("/import")
    fun importItemsFromIcs(
        @AuthenticationPrincipal principal: String,
        @RequestParam("file") file: MultipartFile
    ): ResponseEntity<ImportResultResponse> {
        if (file.isEmpty) {
            return ResponseEntity.badRequest().build()
        }

        // 파일 크기 제한 (10MB)
        if (file.size > 10 * 1024 * 1024) {
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).build()
        }

        val icsContent = file.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
        val importedData = iCalendarService.importFromIcs(icsContent)

        var imported = 0
        var failed = 0

        importedData.forEach { data ->
            try {
                itemService.createItemFromImport(principal, data)
                imported++
            } catch (e: Exception) {
                failed++
                println("Failed to import item: ${e.message}")
            }
        }

        val result = ImportResultResponse(
            imported = imported,
            failed = failed,
            total = importedData.size
        )

        return ResponseEntity(result, HttpStatus.OK)
    }
}

/**
 * Import 결과 응답
 */
data class ImportResultResponse(
    val imported: Int,
    val failed: Int,
    val total: Int
)