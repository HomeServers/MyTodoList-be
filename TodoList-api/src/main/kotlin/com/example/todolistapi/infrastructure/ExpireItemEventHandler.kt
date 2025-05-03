package com.example.todolistapi.infrastructure

import com.example.todolistapi.repository.EventMessageRepository
import com.example.todolistapi.service.ItemService
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import java.lang.RuntimeException

@Service
class ExpireItemEventHandler(
    private val eventMessageRepository: EventMessageRepository,
    private val objectMapper: ObjectMapper,
    private val itemService: ItemService,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Transactional
    @EventListener(ExpireItemEvent::class)
    fun handle2(event: ExpireItemEvent) {
        println("ExpireItemEventHandler.handle2")
        val content = objectMapper.writeValueAsString(event)
        try {
            itemService.expireItem(event.identifier) // unit execute -> batch
            val message = EventMessage(
                content = content,
                eventHash = event.eventHash,
                eventTime = event.eventTime,
                status = EventMessageStatus.SUCCESS
            )
            eventMessageRepository.save(message)
        } catch (e: RuntimeException) {
            logger.error("error on ItemService.expireItem", e)

            val message = EventMessage(
                content = content,
                eventHash = event.eventHash,
                eventTime = event.eventTime,
                status = EventMessageStatus.FAIL
            )
            eventMessageRepository.save(message)
        }
    }
}