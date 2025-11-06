package com.example.taskmanagerapp.data.model

data class Task(
    val id: String,
    val title: String,
    val description: String,
    val priority: Int,
    val status: String,          // "pending", "in_progress", "completed"
    val dueDate: Long,           // Timestamp
    val categoryId: String? = null
)
