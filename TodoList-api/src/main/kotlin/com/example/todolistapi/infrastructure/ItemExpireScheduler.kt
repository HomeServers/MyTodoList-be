package com.example.todolistapi.infrastructure

import com.example.todolistapi.entity.ItemStatus
import com.example.todolistapi.entity.scheduler.ScheduleJob
import com.example.todolistapi.entity.scheduler.ScheduleStatus
import com.example.todolistapi.entity.scheduler.ScheduledHistory
import com.example.todolistapi.repository.ScheduledHistoryRepository
import com.example.todolistapi.service.ItemService
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.lang.RuntimeException
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.UUID

@Service
class ItemExpireScheduler(
    private val itemService: ItemService,
    private val eventPublisher: ApplicationEventPublisher,
    private val scheduledHistoryRepository: ScheduledHistoryRepository
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    /**
     * 매일 자정마다 만료된 [com.example.todolistapi.entity.Item]들을 만료처리한다
     */
    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    fun publishItemExpire() {
        logger.info("Check & Publish expire item event by daily")
        try {
            // step 1
            val persistedHistory = writeScheduledHistory(ScheduleJob.DUE_DATE, ScheduleStatus.ON_GOING)
            // step 2
            executeExpireItems()
            // step 3
            persistedHistory.complete()
        } catch (e: RuntimeException) {
            logger.error("Exception on publishItemExpire. write fail ScheduledHistory")
            writeScheduledHistory(ScheduleJob.DUE_DATE, ScheduleStatus.FAIL)
            throw e
        }
    }

    private fun executeExpireItems() {
        val items = itemService.getItems(listOf(ItemStatus.PENDING, ItemStatus.IN_PROGRESS))
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

    private fun writeScheduledHistory(job: ScheduleJob, status: ScheduleStatus): ScheduledHistory {
        logger.debug("Write scheduled-history {job: $job, status: $status}")
        val history = ScheduledHistory(
            job = job,
            status = status
        )
        return scheduledHistoryRepository.save(history)
    }

    @Transactional
    @EventListener(ApplicationReadyEvent::class)
    fun wrappedUp(event: ApplicationReadyEvent) {
        logger.info("Application ready on ${event.timeTaken}")
        val dueDateHistories = getDueDateHistoryOnFail()
        dueDateHistories.forEach { history ->
            try {
                // step 2
                executeExpireItems()
                // step 3
                history.complete()
            } catch (e: RuntimeException) {
                logger.error("Exception on wrappedUp. continue remain failed schedule job.")
            }
        }
    }

    fun getDueDateHistoryOnFail(): List<ScheduledHistory> {
        val zoneId = ZoneId.of("Asia/Seoul") // KST 기준
        val now = Instant.now()
        val startOfToday = LocalDate.now(zoneId).atStartOfDay(zoneId).toInstant()

        return scheduledHistoryRepository.findAllByStatusAndJobAndCreatedAtBetween(
            ScheduleStatus.FAIL,
            ScheduleJob.DUE_DATE,
            startOfToday,
            now
        )
    }
}