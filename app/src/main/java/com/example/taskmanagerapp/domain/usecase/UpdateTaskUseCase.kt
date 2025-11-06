package com.example.taskmanagerapp.domain.usecase

import com.example.taskmanagerapp.data.model.Task
import com.example.taskmanagerapp.data.repository.TaskRepository
import com.example.taskmanagerapp.data.mapper.toEntity
import javax.inject.Inject

class UpdateTaskUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(task: Task) {
        repository.updateTask(task.toEntity())
        repository.syncTasks() // opcional para mantener sincronización
    }
}
