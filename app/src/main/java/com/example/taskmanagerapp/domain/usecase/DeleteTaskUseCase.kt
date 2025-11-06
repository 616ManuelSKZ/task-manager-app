package com.example.taskmanagerapp.domain.usecase

import com.example.taskmanagerapp.data.model.Task
import com.example.taskmanagerapp.data.repository.TaskRepository
import com.example.taskmanagerapp.data.mapper.toEntity
import javax.inject.Inject

class DeleteTaskUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(task: Task) {
        repository.deleteTask(task.toEntity())
        repository.syncTasks()
    }
}
