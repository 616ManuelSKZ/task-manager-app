package com.example.taskmanagerapp.data.repository

import com.example.taskmanagerapp.data.local.dao.CategoryDao
import com.example.taskmanagerapp.data.local.entity.CategoryEntity
import com.example.taskmanagerapp.data.remote.firebase.CategoryFirebaseSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
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
}
