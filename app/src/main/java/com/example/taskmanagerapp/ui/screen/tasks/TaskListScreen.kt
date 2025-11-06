package com.example.taskmanagerapp.ui.screen.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.taskmanagerapp.data.local.entity.CategoryEntity
import com.example.taskmanagerapp.data.local.entity.TaskEntity
import com.example.taskmanagerapp.data.model.TaskWithCategory
import com.example.taskmanagerapp.viewmodel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    viewModel: TaskViewModel = hiltViewModel(),
    onLogout: (() -> Unit)? = null,
    onTaskSelected: ((TaskEntity) -> Unit)? = null,
    onNavigateToCategories: (() -> Unit)? = null
) {
    val tasksWithCategory by viewModel.tasks.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var filter by remember { mutableStateOf("Todas") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis tareas") },
                actions = {
                    // 🔹 Botón para ir a categorías
                    IconButton(onClick = { onNavigateToCategories?.invoke() }) {
                        Icon(Icons.Default.List, contentDescription = "Categorías", tint = Color.White)
                    }

                    // 🔹 Botón de logout
                    IconButton(onClick = { onLogout?.invoke() }) {
                        Icon(Icons.Default.Logout, contentDescription = "Cerrar sesión", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO: Navegar a creación de tarea */ },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar tarea", tint = Color.White)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
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

@Composable
fun TaskItem(
    task: TaskEntity,
    category: CategoryEntity?,
    onToggleStatus: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = task.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            if (task.description.isNotBlank()) {
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            // 🔹 Mostrar categoría
            category?.let {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(MaterialTheme.shapes.small)
                            .background(Color(android.graphics.Color.parseColor(it.color)))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = it.name,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(android.graphics.Color.parseColor(it.color))
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = when (task.status) {
                        "pending" -> "Pendiente"
                        "in_progress" -> "En progreso"
                        "completed" -> "Completada"
                        else -> "Desconocido"
                    },
                    color = when (task.status) {
                        "completed" -> Color(0xFF4CAF50)
                        "in_progress" -> Color(0xFFFFC107)
                        else -> Color(0xFFF44336)
                    },
                    fontWeight = FontWeight.Medium
                )

                Row {
                    TextButton(onClick = onToggleStatus) {
                        Text("Cambiar estado")
                    }
                    TextButton(onClick = onDelete) {
                        Text("Eliminar", color = Color.Red)
                    }
                }
            }
        }
    }
}
