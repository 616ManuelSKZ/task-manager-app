package com.example.taskmanagerapp.ui.screen.tasks

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.taskmanagerapp.data.local.entity.TaskEntity
import com.example.taskmanagerapp.ui.components.TaskItem
import com.example.taskmanagerapp.viewmodel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    viewModel: TaskViewModel = hiltViewModel(),
    onLogout: (() -> Unit)? = null,
    onTaskSelected: ((TaskEntity) -> Unit)? = null,
    onNavigateToCategories: (() -> Unit)? = null,
    onAddTask: (() -> Unit)? = null
) {
    val tasksWithCategory by viewModel.tasks.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var filter by remember { mutableStateOf("Todas") }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onAddTask?.invoke() },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar tarea", tint = Color.White)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(
                    start = 0.dp,
                    end = 0.dp,
                    top = 0.dp,
                    bottom = padding.calculateBottomPadding()
                )
                .fillMaxSize()
        ) {
            FilterChips(filter = filter, onFilterChange = { filter = it })

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                val filteredTasks = tasksWithCategory.filter {
                    when (filter) {
                        "Pendientes" -> it.task.status == "pending"
                        "En progreso" -> it.task.status == "in_progress"
                        "Completadas" -> it.task.status == "completed"
                        else -> true
                    }
                }

                if (filteredTasks.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No hay tareas", color = Color.Gray)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                    ) {
                        items(filteredTasks) { item ->
                            TaskItem(
                                task = item.task,
                                category = item.category,
                                onToggleStatus = { viewModel.toggleStatus(item.task) },
                                onDelete = { viewModel.deleteTask(item.task) },
                                onClick = { onTaskSelected?.invoke(item.task) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FilterChips(filter: String, onFilterChange: (String) -> Unit) {
    val filters = listOf("Todas", "Pendientes", "En progreso", "Completadas")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        filters.forEach { option ->
            FilterChip(
                selected = filter == option,
                onClick = { onFilterChange(option) },
                label = { Text(option) }
            )
        }
    }
}
