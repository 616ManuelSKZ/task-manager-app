package com.example.taskmanagerapp.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.taskmanagerapp.data.local.dao.CategoryDao
import com.example.taskmanagerapp.data.local.dao.TaskDao
import com.example.taskmanagerapp.data.local.entity.CategoryEntity
import com.example.taskmanagerapp.data.local.entity.TaskEntity

@Database(
    entities = [TaskEntity::class, CategoryEntity::class],
    version = 2,
    exportSchema = false
)
abstract class TaskDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun categoryDao(): CategoryDao
}

