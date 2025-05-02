package com.example.todolistapi.infrastructure

import com.example.todolistapi.service.ItemService
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ItemExpireScheduler(
    private val itemService: ItemService,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    // 자정에 실패하면 복구 로직 필요.
    // 어플리케이션이 올라올 때, 스케줄 작업 재등록 필요?
    // 매일 자정 기간으로 스케줄 tasklet 등록 후 consume 방식으로 변경
    @Scheduled(fixedDelay = 5_000) // 5s -> 1 day
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
}