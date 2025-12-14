package com.example.todolistapi.entity.scheduler

import com.example.todolistapi.entity.BaseEntity
import jakarta.persistence.*

@Entity
@Table
class ScheduledHistory(
    job: ScheduleJob,
    status: ScheduleStatus,
) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L

    @Enumerated(EnumType.STRING)
    val job = job

    @Enumerated(EnumType.STRING)
    var status = status
        protected set

    fun error() {
        this.status = ScheduleStatus.FAIL
    }

    fun complete() {
        this.status = ScheduleStatus.SUCCESS
    }
}

enum class ScheduleJob {
    DUE_DATE, ; // ... etc
}

enum class ScheduleStatus {
//    READY,
    ON_GOING,
    FAIL,
    SUCCESS,
}