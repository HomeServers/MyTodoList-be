package com.example.todolistapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TodoListApiApplication

fun main(args: Array<String>) {
	runApplication<TodoListApiApplication>(*args)
}
