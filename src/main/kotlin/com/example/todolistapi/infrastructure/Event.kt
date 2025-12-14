package com.example.todolistapi.infrastructure

import java.time.Instant

open abstract class Event<T>(
    open val eventTime: Instant,
    open val eventHash: String,
    open val identifier: T,
)