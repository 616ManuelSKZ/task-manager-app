package com.example.taskmanagerapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.taskmanagerapp.data.local.entity.CategoryEntity
import com.example.taskmanagerapp.data.local.entity.TaskEntity
import com.example.taskmanagerapp.util.getDaysUntil
import com.example.taskmanagerapp.util.getDeadlineColor
import com.example.taskmanagerapp.util.getDeadlineLabel

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
                "completed" -> Color(0xFFF1F8E9)       // verde pastel
                "in_progress" -> Color(0xFFFFF3E0)     // naranja pastel
                else -> Color(0xFFFDECEC)              // rojo pastel
            }
        )
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
                task.dueDate?.let {
                    val daysLeft = getDaysUntil(task.dueDate)
                    val deadlineColor = getDeadlineColor(daysLeft)
                    val deadlineLabel = getDeadlineLabel(daysLeft)

                    Text(
                        text = deadlineLabel,
                        color = deadlineColor,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
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