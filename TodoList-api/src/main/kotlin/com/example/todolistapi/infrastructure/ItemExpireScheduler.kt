package com.example.todolistapi.infrastructure

import com.example.todolistapi.repository.EventMessageRepository
import com.example.todolistapi.service.ItemService
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.ApplicationEventPublisher
import org.springframework.scheduling.TaskScheduler
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.LocalDateTime
import java.util.UUID

@Service
class ItemExpireScheduler(
    private val itemService: ItemService,
    private val eventMessageRepository: EventMessageRepository,
    private val eventPublisher: ApplicationEventPublisher,
    @Qualifier(value = "taskScheduler")
    private val taskScheduler: TaskScheduler
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    // 자정에 실패하면 복구 로직 필요.
    // 어플리케이션이 올라올 때, 스케줄 작업 재등록 필요?
    // 매일 자정 기간으로 스케줄 tasklet 등록 후 consume 방식으로 변경
//    @Scheduled(fixedDelay = 5_000) // 5s -> 1 day
    @Transactional
    fun expire() {
        logger.info("check expired item list")
        val items = itemService.getItems()
        val now = LocalDateTime.now()
        items.forEach { item ->
            val dueDate = item.dueDate
            dueDate?.let {
                if (now.isAfter(dueDate)) { // expire
                    itemService.expireItem(item.id) // unit execute -> batch
                }
            }
        }
    }

    // Outbox message pattern 구조 반영
    // message 테이블을 매일 || ApplicationReadyEvent (어플리케이션 구동) 시 scan -> consume
//    @Scheduled(cron = "0 0 0 * * *") // 5s -> 1 day
    @Scheduled(fixedDelay = 100_000)
    fun publishItemExpire() {
        logger.info("Check & Publish expire item event by daily")

        val items = itemService.getItems()
        val now = LocalDateTime.now()
        items.forEach { item ->
            val dueDate = item.dueDate
            dueDate?.let {
                if (now.isAfter(dueDate)) { // expire
                    val event = ExpireItemEvent(
                        eventTime = Instant.now(),
                        eventHash = UUID.randomUUID().toString(),
                        identifier = item.id
                    )
                    eventPublisher.publishEvent(event)
                }
            }
        }
    }
}