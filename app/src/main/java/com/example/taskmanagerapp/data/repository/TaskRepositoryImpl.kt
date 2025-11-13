package com.example.taskmanagerapp.data.repository

import android.util.Log
import com.example.taskmanagerapp.data.local.dao.TaskDao
import com.example.taskmanagerapp.data.local.entity.TaskEntity
import com.example.taskmanagerapp.data.remote.firebase.FirebaseDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao,
    private val firebaseDataSource: FirebaseDataSource
) : TaskRepository {

    override fun getAllTasks(): Flow<List<TaskEntity>> = taskDao.getAllTasks()

    override suspend fun insertTask(task: TaskEntity) {
        withContext(Dispatchers.IO) {
            taskDao.insertTask(task)
            try {
                firebaseDataSource.uploadTask(task)
            } catch (e: Exception) {
                Log.e("TaskRepo", "firebase upload failed: ${e.message}", e)
            }
        }
    }

    override suspend fun updateTask(task: TaskEntity) {
        taskDao.updateTask(task)
        firebaseDataSource.uploadTask(task)
    }

    override suspend fun deleteTask(task: TaskEntity) {
        taskDao.deleteTask(task)
        firebaseDataSource.deleteTask(task.id)
    }

    override suspend fun syncTasks() {
        withContext(Dispatchers.IO) {
            val localTasks = taskDao.getAllTasksOnce()
            val remoteResult = firebaseDataSource.getTasks()

            if (remoteResult.isSuccess) {
                val remoteTasks = remoteResult.getOrNull().orEmpty()

                localTasks.forEach { localTask ->
                    val existsRemote = remoteTasks.any { it.id == localTask.id }
                    if (!existsRemote) {
                        firebaseDataSource.uploadTask(localTask)
                    }
                }

                remoteTasks.forEach { remoteTask ->
                    taskDao.insertTask(remoteTask)
                }
            }
        }
    }

    fun startRealtimeSync() {
        firebaseDataSource.observeTasks { remoteTasks ->
            kotlinx.coroutines.CoroutineScope(Dispatchers.IO).launch {
                remoteTasks.forEach { remoteTask ->
                    try {
                        val idToUse = remoteTask.id.ifEmpty { UUID.randomUUID().toString() }
                        val normalized = remoteTask.copy(id = idToUse, dueDate = remoteTask.dueDate ?: System.currentTimeMillis())
                        taskDao.insertTask(normalized)
                    } catch (e: Exception) {
                        Log.e("TaskRepo", "Error insertando task desde realtime: ${e.message}", e)
                    }
                }
            }
        }
    }

    override suspend fun syncTasksToFirebase() {
        withContext(Dispatchers.IO) {
            val localTasks = taskDao.getAllTasksOnce()
            localTasks.forEach { task ->
                firebaseDataSource.uploadTask(task)
            }
        }
    }
}
