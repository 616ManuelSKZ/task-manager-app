package com.example.taskmanagerapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val description: String = "",
    val priority: Int = 0,
    val status: String = "pending", // pending, in_progress, completed
    val dueDate: Long = System.currentTimeMillis(),
    val categoryId: String? = null,
    val isSynced: Boolean = false // 🔹 ayuda a saber si ya se subió a Firestore
)
