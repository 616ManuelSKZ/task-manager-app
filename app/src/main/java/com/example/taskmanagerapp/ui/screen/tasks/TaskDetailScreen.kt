package com.example.taskmanagerapp.ui.screen.tasks

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
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
import com.example.taskmanagerapp.data.local.entity.CategoryEntity
import com.example.taskmanagerapp.data.local.entity.TaskEntity
import com.example.taskmanagerapp.ui.components.AppTopBar
import com.example.taskmanagerapp.ui.components.DatePickerCompose
import com.example.taskmanagerapp.viewmodel.CategoryViewModel
import com.example.taskmanagerapp.viewmodel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    task: TaskEntity,
    onBack: () -> Unit,
    viewModel: TaskViewModel = hiltViewModel(),
    categoryViewModel: CategoryViewModel = hiltViewModel()
) {
    var title by remember { mutableStateOf(task.title) }
    var description by remember { mutableStateOf(task.description) }
    var priority by remember { mutableStateOf(task.priority) }
    var status by remember { mutableStateOf(task.status) }
    var selectedCategoryId by remember { mutableStateOf(task.categoryId ?: "") }
    var dueDate by remember { mutableStateOf(task.dueDate) }

    val categories by categoryViewModel.categories.collectAsState()

    LaunchedEffect(Unit) {
        categoryViewModel.loadCategories()
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Editar tarea",
                onBack = onBack,
                actions = {
                    IconButton(onClick = {
                        viewModel.deleteTask(task)
                        onBack()
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.White)
                    }
                    IconButton(onClick = {
                        val updated = task.copy(
                            title = title,
                            description = description,
                            priority = priority,
                            status = status,
                            categoryId = selectedCategoryId,
                            dueDate = dueDate
                        )
                        viewModel.updateTask(updated)
                        onBack()
                    }) {
                        Icon(Icons.Default.Save, contentDescription = "Guardar", tint = Color.White)
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
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
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3,
            )

            Text("Prioridad:", fontWeight = FontWeight.Bold)
            PrioritySelector(priority) { priority = it }

            Text("Estado:", fontWeight = FontWeight.Bold)
            StatusSelector(status) { status = it }

            Text("Categoría:", fontWeight = FontWeight.Bold)
            CategoryDropdown(
                categories = categories,
                selectedCategoryId = selectedCategoryId,
                onSelect = { selectedCategoryId = it }
            )

            Text("Fecha de vencimiento:", fontWeight = FontWeight.Bold)
            DatePickerCompose(
                selectedDate = dueDate,
                onDateSelected = { dueDate = it }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun CategoryDropdown(
    categories: List<CategoryEntity>,
    selectedCategoryId: String,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedCategory = categories.find { it.id == selectedCategoryId }

    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Text(selectedCategory?.name ?: "Seleccionar categoría")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            categories.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category.name) },
                    onClick = {
                        onSelect(category.id)
                        expanded = false
                    }
                )
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
            steps = 3
        )
        Text("Nivel: $currentPriority")
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
