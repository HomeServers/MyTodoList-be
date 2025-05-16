package com.example.todolistapi.controller.response

import java.time.Instant

abstract class Response<T>(
    open val data : T?
)

data class SuccessResponse<T> (
    override val data: T?
) : Response<T>(data)

data class ErrorResponse<T>(
    override val data : T?,
    val code : String,
    val status: Int,
    val message: String?,
    val time: Instant
) : Response<T>(data)