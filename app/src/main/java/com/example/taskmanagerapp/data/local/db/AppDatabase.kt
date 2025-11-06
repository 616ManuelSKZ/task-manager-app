package com.example.taskmanagerapp.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.taskmanagerapp.data.local.dao.TaskDao
import com.example.taskmanagerapp.data.local.entity.TaskEntity

@Database(
    entities = [TaskEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
}
