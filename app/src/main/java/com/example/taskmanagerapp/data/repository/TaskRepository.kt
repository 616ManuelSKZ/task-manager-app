package com.example.taskmanagerapp.data.repository

import com.example.taskmanagerapp.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

interface TaskRepository {

    // Local (Room)
    fun getAllTasks(): Flow<List<TaskEntity>>
    suspend fun insertTask(task: TaskEntity)
    suspend fun updateTask(task: TaskEntity)
    suspend fun deleteTask(task: TaskEntity)

    // sincronizar Room ↔ Firebase
    suspend fun syncTasks()
    suspend fun syncTasksToFirebase()
}
