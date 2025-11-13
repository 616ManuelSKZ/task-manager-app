package com.example.taskmanagerapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val description: String = "",
    val status: String = "pending",
    val priority: Int = 1,
    val dueDate: Long? = null,
    val categoryId: String? = null,
    val synced: Boolean = false
)
