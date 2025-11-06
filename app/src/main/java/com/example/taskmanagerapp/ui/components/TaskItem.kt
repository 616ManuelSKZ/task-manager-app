package com.example.taskmanagerapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.taskmanagerapp.data.local.entity.CategoryEntity
import com.example.taskmanagerapp.data.local.entity.TaskEntity

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
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (task.status) {
                "completed" -> Color(0xFFE8F5E9)
                "in_progress" -> Color(0xFFFFF8E1)
                else -> Color(0xFFFFEBEE)
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // 🔹 Título de la tarea
            Text(
                text = task.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // 🔹 Descripción (si existe)
            if (task.description.isNotBlank()) {
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            // 🔹 Categoría (color + nombre)
            category?.let {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(RoundedCornerShape(50))
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

            Divider(modifier = Modifier.padding(vertical = 4.dp))

            // 🔹 Estado y acciones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
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

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
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
