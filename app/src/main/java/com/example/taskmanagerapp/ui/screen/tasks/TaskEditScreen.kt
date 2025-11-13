package com.example.taskmanagerapp.ui.screen.tasks

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.taskmanagerapp.data.local.entity.CategoryEntity
import com.example.taskmanagerapp.data.local.entity.TaskEntity
import com.example.taskmanagerapp.ui.components.AppTopBar
import com.example.taskmanagerapp.ui.components.DatePickerCompose
import com.example.taskmanagerapp.viewmodel.CategoryViewModel
import com.example.taskmanagerapp.viewmodel.TaskViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskEditScreen(
    onSave: () -> Unit,
    onCancel: () -> Unit,
    existingTask: TaskEntity? = null,
    taskViewModel: TaskViewModel = hiltViewModel(),
    categoryViewModel: CategoryViewModel = hiltViewModel()
) {
    var title by remember { mutableStateOf(existingTask?.title ?: "") }
    var description by remember { mutableStateOf(existingTask?.description ?: "") }
    var priority by remember { mutableStateOf(existingTask?.priority ?: 1) }
    var status by remember { mutableStateOf(existingTask?.status ?: "pending") }
    var selectedCategoryId by remember { mutableStateOf(existingTask?.categoryId ?: "") }
    var dueDate by remember { mutableStateOf(existingTask?.dueDate) }

    val categories by categoryViewModel.categories.collectAsState()

    LaunchedEffect(Unit) {
        categoryViewModel.loadCategories()
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Nueva tarea",
                onBack = onCancel
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)   // <-- Aquí está el problema
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
                minLines = 3,
            )

            Text("Prioridad: $priority", fontWeight = FontWeight.Bold)
            Slider(
                value = priority.toFloat(),
                onValueChange = { priority = it.toInt() },
                valueRange = 1f..5f,
                steps = 3
            )

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

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                OutlinedButton(onClick = onCancel) {
                    Text("Cancelar")
                }

                Button(
                    onClick = {
                        if (title.isNotBlank()) {

                            val task = existingTask?.copy(
                                title = title,
                                description = description,
                                priority = priority,
                                status = status,
                                categoryId = selectedCategoryId,
                                dueDate = dueDate
                            ) ?: TaskEntity(
                                id = UUID.randomUUID().toString(),
                                title = title,
                                description = description,
                                priority = priority,
                                status = status,
                                categoryId = selectedCategoryId,
                                dueDate = dueDate ?: System.currentTimeMillis()
                            )

                            if (existingTask == null)
                                taskViewModel.addTask(task)
                            else
                                taskViewModel.updateTask(task)

                            onSave()
                        }
                    }
                ) {
                    Text("Guardar")
                }
            }
        }
    }
}

@Composable
fun CategoryDropdown(
    categories: List<CategoryEntity>,
    selectedCategoryId: String?,
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
