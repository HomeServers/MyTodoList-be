package com.example.todolistapi.repository

import com.example.todolistapi.entity.scheduler.ScheduleJob
import com.example.todolistapi.entity.scheduler.ScheduleStatus
import com.example.todolistapi.entity.scheduler.ScheduledHistory
import org.springframework.data.jpa.repository.JpaRepository
import java.time.Instant

interface ScheduledHistoryRepository : JpaRepository<ScheduledHistory, Long> {
    fun findAllByStatusAndJobAndCreatedAtBetween(
        status: ScheduleStatus,
        job: ScheduleJob,
        startOfToday: Instant,
        now: Instant
    ): List<ScheduledHistory>

    fun countByStatusAndCreatedAtBetween(status: ScheduleStatus, startOfToday: Instant, now: Instant): Long
}