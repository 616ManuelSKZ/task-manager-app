package com.example.taskmanagerapp.data.remote.firebase

import android.util.Log
import com.example.taskmanagerapp.data.local.entity.CategoryEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryFirebaseSource @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    private val collection get() = firestore
        .collection("users")
        .document(auth.currentUser?.uid ?: "unknown")
        .collection("categories")

    suspend fun getCategories(): Result<List<CategoryEntity>> {
        return try {
            val snapshot = collection.get().await()
            val categories = snapshot.toObjects(CategoryEntity::class.java)
            Result.success(categories)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun observeCategories(onChange: (List<CategoryEntity>) -> Unit) {
        collection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("CategoryFS", "observeCategories error: ${error.message}", error)
                return@addSnapshotListener
            }

            try {
                val categories = snapshot?.toObjects(CategoryEntity::class.java).orEmpty()
                // normalizar: asegurar que cada categoría tenga id/name/color válidos
                val fixed = categories.map { cat ->
                    cat.copy(
                        id = cat.id.ifEmpty { UUID.randomUUID().toString() },
                        name = cat.name ?: "",
                        color = cat.color.ifEmpty { "#2196F3" }
                    )
                }
                onChange(fixed)
            } catch (e: Exception) {
                Log.e("CategoryFS", "observeCategories parse error", e)
            }
        }
    }

    suspend fun uploadCategory(category: CategoryEntity): Result<Unit> {
        return try {
            collection.document(category.id).set(category).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteCategory(categoryId: String): Result<Unit> {
        return try {
            collection.document(categoryId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
