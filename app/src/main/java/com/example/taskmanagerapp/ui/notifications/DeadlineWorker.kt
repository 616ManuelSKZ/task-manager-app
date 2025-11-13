package com.example.taskmanagerapp.ui.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.taskmanagerapp.data.local.db.TaskDatabase
import com.example.taskmanagerapp.util.getDaysUntil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DeadlineWorker(
    private val appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {

        val dao = TaskDatabase.getInstance(appContext).taskDao()

        val tasks = dao.getAllTasksOnce() // ahora sí funciona

        tasks.forEach { task ->
            val daysLeft = getDaysUntil(task.dueDate)

            when (daysLeft) {
                -1 -> NotificationHelper.showNotification(
                    appContext, "Tarea vencida",
                    "La tarea \"${task.title}\" venció ayer",
                    task.id.hashCode()
                )
                0 -> NotificationHelper.showNotification(
                    appContext, "Vence hoy",
                    "La tarea \"${task.title}\" vence hoy",
                    task.id.hashCode()
                )
                1 -> NotificationHelper.showNotification(
                    appContext, "Vence mañana",
                    "La tarea \"${task.title}\" vence mañana",
                    task.id.hashCode()
                )
            }
        }

        Result.success()
    }
}
