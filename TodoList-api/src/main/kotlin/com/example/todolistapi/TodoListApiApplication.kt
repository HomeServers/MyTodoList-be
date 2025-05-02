package com.example.todolistapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class TodoListApiApplication

fun main(args: Array<String>) {
	runApplication<TodoListApiApplication>(*args)
}
