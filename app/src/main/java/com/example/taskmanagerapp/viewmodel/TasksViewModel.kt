package com.example.taskmanagerapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmanagerapp.data.local.entity.TaskEntity
import com.example.taskmanagerapp.data.model.TaskWithCategory
import com.example.taskmanagerapp.data.repository.CategoryRepository
import com.example.taskmanagerapp.data.repository.CategoryRepositoryImpl
import com.example.taskmanagerapp.data.repository.TaskRepository
import com.example.taskmanagerapp.data.repository.TaskRepositoryImpl
import com.example.taskmanagerapp.util.getDaysUntil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _tasks = MutableStateFlow<List<TaskWithCategory>>(emptyList())
    val tasks: StateFlow<List<TaskWithCategory>> = _tasks.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        viewModelScope.launch {

            taskRepository.syncTasks()
        }

        if (taskRepository is TaskRepositoryImpl) {
            taskRepository.startRealtimeSync()
        }

        if (categoryRepository is CategoryRepositoryImpl) {

            categoryRepository.startRealtimeSync()

            viewModelScope.launch {
                try {
                    categoryRepository.syncCategories()
                } catch (e: Exception) {
                    // log para depuración
                    android.util.Log.e("TaskVM", "syncCategories fallo: ${e.message}", e)
                }
            }
        }

        loadTasks()
    }

    fun loadTasks() {
        viewModelScope.launch {
            _isLoading.value = true

            combine(
                taskRepository.getAllTasks(),
                categoryRepository.getAllCategories()
            ) { tasks, categories ->
                tasks.map { task ->
                    val category = categories.find { it.id == task.categoryId }
                    TaskWithCategory(task, category)
                }
            }.collect { combined ->
                _tasks.value = combined.sortedWith(
                    compareBy<TaskWithCategory> {
                        val days = getDaysUntil(it.task.dueDate)
                        when {
                            days == null -> 1000
                            days < 0 -> -100
                            else -> days
                        }
                    }
                )
                _isLoading.value = false
            }
        }
    }

    fun addTask(task: TaskEntity) {
        viewModelScope.launch {
            taskRepository.insertTask(task)
            taskRepository.syncTasks()
        }
    }

    fun updateTask(task: TaskEntity) {
        viewModelScope.launch {
            taskRepository.updateTask(task)
            taskRepository.syncTasks()
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            taskRepository.deleteTask(task)
            taskRepository.syncTasks()
        }
    }

    fun toggleStatus(task: TaskEntity) {
        val newStatus = when (task.status) {
            "pending" -> "in_progress"
            "in_progress" -> "completed"
            else -> "pending"
        }
        val updatedTask = task.copy(status = newStatus)
        updateTask(updatedTask)
    }

    fun createEmptyTask() {
        val emptyTask = TaskEntity(
            title = "Nueva tarea",
            description = "",
            priority = 1,
            status = "pending",
            dueDate = System.currentTimeMillis()
        )
        addTask(emptyTask)
    }
}
