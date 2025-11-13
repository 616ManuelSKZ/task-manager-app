package com.example.taskmanagerapp.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.taskmanagerapp.data.local.dao.CategoryDao
import com.example.taskmanagerapp.data.local.dao.TaskDao
import com.example.taskmanagerapp.data.local.entity.CategoryEntity
import com.example.taskmanagerapp.data.local.entity.TaskEntity

@Database(
    entities = [TaskEntity::class, CategoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class TaskDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        @Volatile
        private var INSTANCE: TaskDatabase? = null

        fun getInstance(context: Context): TaskDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    TaskDatabase::class.java,
                    "tasks_db"
                ).fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
