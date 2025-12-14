package com.example.todolistapi.infrastructure

import java.time.Instant

data class ExpireItemEvent(
    override val eventTime: Instant,
    override val eventHash: String,
    override val identifier: Long
) : Event<Long>(eventTime, eventHash, identifier)