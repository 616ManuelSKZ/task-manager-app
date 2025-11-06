package com.example.taskmanagerapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PriorityChip(priority: Int, onSelect: (Int) -> Unit) {
    val color = when (priority) {
        1 -> Color(0xFF4CAF50) // Verde (baja)
        2 -> Color(0xFF8BC34A)
        3 -> Color(0xFFFFC107) // Amarillo (media)
        4 -> Color(0xFFFF9800)
        5 -> Color(0xFFF44336) // Rojo (alta)
        else -> MaterialTheme.colorScheme.primary
    }

    Box(
        modifier = Modifier
            .background(color = color, shape = MaterialTheme.shapes.small)
            .clickable { onSelect(priority) }
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text = "P$priority", color = Color.White)
    }
}
