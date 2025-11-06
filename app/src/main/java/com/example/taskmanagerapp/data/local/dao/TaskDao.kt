package com.example.taskmanagerapp.data.local.dao

import androidx.room.*
import com.example.taskmanagerapp.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    // 🔹 Observa cambios en tiempo real (Flow)
    @Query("SELECT * FROM tasks ORDER BY dueDate ASC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    // 🔹 Obtiene todas las tareas una sola vez (sin Flow)
    @Query("SELECT * FROM tasks ORDER BY dueDate ASC")
    suspend fun getAllTasksOnce(): List<TaskEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    // 🔹 Borrar todo (útil si reseteas la base local)
    @Query("DELETE FROM tasks")
    suspend fun deleteAllTasks()
}
