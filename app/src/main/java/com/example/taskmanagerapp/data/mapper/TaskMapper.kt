package com.example.taskmanagerapp.data.mapper

import com.example.taskmanagerapp.data.local.entity.TaskEntity
import com.example.taskmanagerapp.data.model.Task

/**
 * 🔹 Convierte un TaskEntity (Room) a Task (modelo de dominio)
 */
fun TaskEntity.toDomain(): Task = Task(
    id = id,
    title = title,
    description = description,
    priority = priority,
    status = status,
    dueDate = dueDate,
    categoryId = categoryId
)

/**
 * 🔹 Convierte un Task (modelo de dominio) a TaskEntity (para Room o Firebase)
 */
fun Task.toEntity(): TaskEntity = TaskEntity(
    id = id,
    title = title,
    description = description,
    priority = priority,
    status = status,
    dueDate = dueDate,
    categoryId = categoryId
)
