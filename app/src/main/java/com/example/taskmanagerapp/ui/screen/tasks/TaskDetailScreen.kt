package com.example.taskmanagerapp.ui.screen.tasks

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.taskmanagerapp.data.local.entity.TaskEntity
import com.example.taskmanagerapp.viewmodel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    task: TaskEntity,
    onBack: () -> Unit,
    viewModel: TaskViewModel = hiltViewModel()
) {
    var title by remember { mutableStateOf(task.title) }
    var description by remember { mutableStateOf(task.description) }
    var priority by remember { mutableStateOf(task.priority) }
    var status by remember { mutableStateOf(task.status) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar tarea") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.deleteTask(task)
                        onBack()
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.White)
                    }
                    IconButton(onClick = {
                        val updatedTask = task.copy(
                            title = title,
                            description = description,
                            priority = priority,
                            status = status
                        )
                        viewModel.updateTask(updatedTask)
                        onBack()
                    }) {
                        Icon(Icons.Default.Save, contentDescription = "Guardar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth()
            )

            Text("Prioridad:", fontWeight = FontWeight.Bold)
            PrioritySelector(priority) { priority = it }

            Text("Estado:", fontWeight = FontWeight.Bold)
            StatusSelector(status) { status = it }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val updatedTask = task.copy(
                        title = title,
                        description = description,
                        priority = priority,
                        status = status
                    )
                    viewModel.updateTask(updatedTask)
                    onBack()
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Guardar cambios")
            }
        }
    }
}

@Composable
fun PrioritySelector(currentPriority: Int, onPriorityChange: (Int) -> Unit) {
    Column {
        Slider(
            value = currentPriority.toFloat(),
            onValueChange = { onPriorityChange(it.toInt()) },
            valueRange = 1f..5f,
            steps = 3,
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary
            )
        )
        Text(
            text = "Nivel: $currentPriority",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun StatusSelector(currentStatus: String, onStatusChange: (String) -> Unit) {
    val statuses = listOf(
        "pending" to "Pendiente",
        "in_progress" to "En progreso",
        "completed" to "Completada"
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        statuses.forEach { (key, label) ->
            FilterChip(
                selected = currentStatus == key,
                onClick = { onStatusChange(key) },
                label = { Text(label) }
            )
        }
    }
}
