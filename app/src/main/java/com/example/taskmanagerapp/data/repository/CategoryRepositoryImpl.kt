package com.example.taskmanagerapp.data.repository

import android.util.Log
import com.example.taskmanagerapp.data.local.dao.CategoryDao
import com.example.taskmanagerapp.data.local.entity.CategoryEntity
import com.example.taskmanagerapp.data.remote.firebase.CategoryFirebaseSource
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
class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao,
    private val remoteSource: CategoryFirebaseSource
) : CategoryRepository {

    override fun getAllCategories(): Flow<List<CategoryEntity>> =
        categoryDao.getAllCategories()

    override suspend fun insertCategory(category: CategoryEntity) {
        categoryDao.insertCategory(category)
        remoteSource.uploadCategory(category)
    }

    override suspend fun deleteCategory(category: CategoryEntity) {
        categoryDao.deleteCategory(category)
        remoteSource.deleteCategory(category.id)
    }

    override suspend fun syncCategories() {
        withContext(Dispatchers.IO) {
            val remoteResult = remoteSource.getCategories()
            if (remoteResult.isSuccess) {
                val remoteCategories = remoteResult.getOrNull().orEmpty()
                remoteCategories.forEach { category ->
                    categoryDao.insertCategory(category)
                }
            }
        }
    }

    fun startRealtimeSync() {
        remoteSource.observeCategories { remoteCategories ->
            android.util.Log.d("CategoryRepo", "observeCategories -> received ${remoteCategories.size} items")
            kotlinx.coroutines.CoroutineScope(Dispatchers.IO).launch {
                remoteCategories.forEach { cat ->
                    try {
                        val idToUse = cat.id.ifEmpty { UUID.randomUUID().toString() }
                        val normalized = cat.copy(id = idToUse, name = cat.name ?: "", color = cat.color.ifEmpty { "#2196F3" })
                        categoryDao.insertCategory(normalized)
                        android.util.Log.d("CategoryRepo", "inserted category ${normalized.id}")
                    } catch (e: Exception) {
                        android.util.Log.e("CategoryRepo", "Error insert realtime category: ${e.message}", e)
                    }
                }
            }
        }
    }
}
