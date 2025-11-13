package com.example.taskmanagerapp.data.mapper

import com.example.taskmanagerapp.data.local.entity.TaskEntity
import com.example.taskmanagerapp.data.model.Task

fun TaskEntity.toDomain(): Task = Task(
    id = id,
    title = title,
    description = description,
    priority = priority,
    status = status,
    dueDate = dueDate,
    categoryId = categoryId
)

fun Task.toEntity(): TaskEntity = TaskEntity(
    id = id,
    title = title,
    description = description,
    priority = priority,
    status = status,
    dueDate = dueDate,
    categoryId = categoryId
)
