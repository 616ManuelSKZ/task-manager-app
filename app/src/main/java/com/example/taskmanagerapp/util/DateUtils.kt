package com.example.taskmanagerapp.util

import androidx.compose.ui.graphics.Color
import java.util.*
import kotlin.math.abs

fun getDaysUntil(dateMillis: Long?): Int? {
    if (dateMillis == null) return null

    val cal1 = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    val cal2 = Calendar.getInstance().apply {
        timeInMillis = dateMillis
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    val diff = cal2.timeInMillis - cal1.timeInMillis
    return (diff / (1000 * 60 * 60 * 24)).toInt()
}

fun getDeadlineColor(days: Int?): Color =
    when {
        days == null -> Color.Gray
        days < 0 -> Color(0xFFD32F2F)       // Rojo vencido
        days == 0 -> Color(0xFFFFA000)      // Amarillo (vence hoy)
        days < 3 -> Color(0xFFFFC107)       // Amarillo claro (urgente)
        else -> Color(0xFF4CAF50)           // Verde
    }

fun getDeadlineLabel(days: Int?): String =
    when {
        days == null -> "Sin fecha límite"
        days < 0 -> "⛔ Vencida hace ${abs(days)} días"
        days == 0 -> "⚠️ Vence hoy"
        days == 1 -> "📅 Mañana"
        else -> "📅 En $days días"
    }
