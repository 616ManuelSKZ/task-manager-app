package com.example.taskmanagerapp.domain.usecase

import com.example.taskmanagerapp.data.mapper.toDomain
import com.example.taskmanagerapp.data.repository.TaskRepository
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetTasksUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    operator fun invoke() = repository.getAllTasks().map { list ->
        list.map { it.toDomain() }
    }
}
