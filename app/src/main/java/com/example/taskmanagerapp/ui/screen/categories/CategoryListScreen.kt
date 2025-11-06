package com.example.taskmanagerapp.ui.screen.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.taskmanagerapp.data.local.entity.CategoryEntity
import com.example.taskmanagerapp.viewmodel.CategoryViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryListScreen(
    viewModel: CategoryViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val categories by viewModel.categories.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Categorías") },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
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
                onClick = { showDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar categoría", tint = Color.White)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (categories.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay categorías aún", color = Color.Gray)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories) { category ->
                        CategoryItem(
                            category = category,
                            onDelete = { viewModel.deleteCategory(category) }
                        )
                    }
                }
            }
        }
    }

    if (showDialog) {
        AddCategoryDialog(
            onDismiss = { showDialog = false },
            onSave = { name, color ->
                val category = CategoryEntity(
                    id = UUID.randomUUID().toString(),
                    name = name,
                    color = color
                )
                viewModel.addCategory(category)
                showDialog = false
            }
        )
    }
}

@Composable
fun CategoryItem(category: CategoryEntity, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(Color(android.graphics.Color.parseColor(category.color)))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red)
            }
        }
    }
}

@Composable
fun AddCategoryDialog(onDismiss: () -> Unit, onSave: (String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf("#2196F3") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                if (name.isNotBlank()) {
                    onSave(name, selectedColor)
                }
            }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        },
        title = { Text("Nueva categoría") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre de la categoría") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Color:", fontWeight = FontWeight.Bold)
                ColorSelector(selectedColor = selectedColor, onColorSelected = { selectedColor = it })
            }
        }
    )
}

@Composable
fun ColorSelector(selectedColor: String, onColorSelected: (String) -> Unit) {
    val colors = listOf("#2196F3", "#4CAF50", "#FF9800", "#F44336", "#9C27B0")
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        colors.forEach { colorHex ->
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(Color(android.graphics.Color.parseColor(colorHex)))
                    .clickable { onColorSelected(colorHex) }
                    .then(
                        if (colorHex == selectedColor)
                            Modifier.border(2.dp, Color.Black, MaterialTheme.shapes.small)
                        else Modifier
                    )
            )
        }
    }
}
