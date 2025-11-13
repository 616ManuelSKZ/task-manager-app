package com.example.taskmanagerapp.data.model

data class Task(
    val id: String,
    val title: String,
    val description: String,
    val priority: Int,
    val status: String,
    val dueDate: Long? = null,
    val categoryId: String? = null
)
