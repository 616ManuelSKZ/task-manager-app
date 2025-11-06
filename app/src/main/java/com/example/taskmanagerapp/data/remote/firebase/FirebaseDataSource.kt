package com.example.taskmanagerapp.data.remote.firebase

import com.example.taskmanagerapp.data.local.entity.TaskEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseDataSource @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    private fun getUserTasksCollection() =
        firestore.collection("users")
            .document(auth.currentUser?.uid ?: "")
            .collection("tasks")

    suspend fun uploadTask(task: TaskEntity): Result<Unit> {
        val userId = auth.currentUser?.uid ?: return Result.failure(Exception("User not authenticated"))
        return try {
            getUserTasksCollection()
                .document(task.id)
                .set(task)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTasks(): Result<List<TaskEntity>> {
        val userId = auth.currentUser?.uid ?: return Result.failure(Exception("User not authenticated"))
        return try {
            val snapshot = getUserTasksCollection().get().await()
            val tasks = snapshot.toObjects(TaskEntity::class.java)
            Result.success(tasks)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteTask(taskId: String): Result<Unit> {
        val userId = auth.currentUser?.uid ?: return Result.failure(Exception("User not authenticated"))
        return try {
            getUserTasksCollection().document(taskId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
