package com.example.taskmanagerapp.ui.components

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DatePickerCompose(
    selectedDate: Long?,
    onDateSelected: (Long) -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    selectedDate?.let { calendar.timeInMillis = it }

    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val dateText = remember(selectedDate) {
        selectedDate?.let { dateFormat.format(Date(it)) } ?: "Seleccionar fecha"
    }

    Button(onClick = {
        val datePicker = DatePickerDialog(
            context,
            { _: DatePicker, year: Int, month: Int, day: Int ->
                val newDate = Calendar.getInstance()
                newDate.set(year, month, day)
                onDateSelected(newDate.timeInMillis)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }) {
        Text(dateText)
    }
}
