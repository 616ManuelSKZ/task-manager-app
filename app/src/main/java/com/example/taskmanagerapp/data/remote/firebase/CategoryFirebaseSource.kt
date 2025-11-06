package com.example.taskmanagerapp.data.remote.firebase

import com.example.taskmanagerapp.data.local.entity.CategoryEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryFirebaseSource @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    private fun getUserId(): String? = auth.currentUser?.uid

    suspend fun uploadCategory(category: CategoryEntity): Result<Unit> {
        val uid = getUserId() ?: return Result.failure(Exception("Usuario no autenticado"))
        return try {
            firestore.collection("users")
                .document(uid)
                .collection("categories")
                .document(category.id)
                .set(category)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteCategory(categoryId: String): Result<Unit> {
        val uid = getUserId() ?: return Result.failure(Exception("Usuario no autenticado"))
        return try {
            firestore.collection("users")
                .document(uid)
                .collection("categories")
                .document(categoryId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCategories(): Result<List<CategoryEntity>> {
        val uid = getUserId() ?: return Result.failure(Exception("Usuario no autenticado"))
        return try {
            val snapshot = firestore.collection("users")
                .document(uid)
                .collection("categories")
                .get()
                .await()

            val categories = snapshot.toObjects(CategoryEntity::class.java)
            Result.success(categories)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
