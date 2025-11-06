package com.example.taskmanagerapp.data.model

import com.example.taskmanagerapp.data.local.entity.TaskEntity
import com.example.taskmanagerapp.data.local.entity.CategoryEntity

data class TaskWithCategory(
    val task: TaskEntity,
    val category: CategoryEntity? = null
)
