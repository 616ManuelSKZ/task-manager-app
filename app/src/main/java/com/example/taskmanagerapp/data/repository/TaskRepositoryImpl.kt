package com.example.taskmanagerapp.data.repository

import com.example.taskmanagerapp.data.local.dao.TaskDao
import com.example.taskmanagerapp.data.local.entity.TaskEntity
import com.example.taskmanagerapp.data.remote.firebase.FirebaseDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao,
    private val firebaseDataSource: FirebaseDataSource
) : TaskRepository {

    override fun getAllTasks(): Flow<List<TaskEntity>> = taskDao.getAllTasks()

    override suspend fun insertTask(task: TaskEntity) {
        taskDao.insertTask(task)
    }

    override suspend fun updateTask(task: TaskEntity) {
        taskDao.updateTask(task)
    }

    override suspend fun deleteTask(task: TaskEntity) {
        taskDao.deleteTask(task)
    }

    // 🔹 Sincronización bidireccional
    override suspend fun syncTasks() {
        withContext(Dispatchers.IO) {
            // 1️⃣ Obtener todas las tareas locales
            val localTasks = taskDao.getAllTasksOnce()

            // 2️⃣ Descargar las tareas desde Firebase
            val remoteResult = firebaseDataSource.getTasks()
            if (remoteResult.isSuccess) {
                val remoteTasks = remoteResult.getOrNull().orEmpty()

                // 3️⃣ Fusionar: subir las locales que no están en la nube
                localTasks.forEach { localTask ->
                    val existsRemote = remoteTasks.any { it.id == localTask.id }
                    if (!existsRemote) {
                        firebaseDataSource.uploadTask(localTask)
                    }
                }

                // 4️⃣ Insertar/actualizar en Room las que vienen desde la nube
                remoteTasks.forEach { remoteTask ->
                    taskDao.insertTask(remoteTask)
                }
            }
        }
    }

    // 🔹 Sincronización unidireccional (solo de local → Firebase)
    override suspend fun syncTasksToFirebase() {
        withContext(Dispatchers.IO) {
            val localTasks = taskDao.getAllTasksOnce()
            localTasks.forEach { task ->
                firebaseDataSource.uploadTask(task)
            }
        }
    }

}
