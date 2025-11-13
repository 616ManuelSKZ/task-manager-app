package com.example.taskmanagerapp.data.remote.firebase

import android.util.Log
import com.example.taskmanagerapp.data.local.entity.TaskEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseDataSource @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    private val collection get() = firestore
        .collection("users")
        .document(auth.currentUser?.uid ?: "unknown")
        .collection("tasks")

    suspend fun getTasks(): Result<List<TaskEntity>> {
        return try {
            val snapshot = collection.get().await()
            val tasks = snapshot.toObjects(TaskEntity::class.java)
            Result.success(tasks)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun observeTasks(onChange: (List<TaskEntity>) -> Unit) {
        collection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("FirebaseDS", "observeTasks error: ${error.message}", error)
                return@addSnapshotListener
            }
            try {
                val tasks = snapshot?.toObjects(TaskEntity::class.java).orEmpty()
                onChange(tasks)
            } catch (e: Exception) {
                Log.e("FirebaseDS", "observeTasks parsing error", e)
            }
        }
    }

    suspend fun uploadTask(task: TaskEntity): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid
                ?: return Result.failure(Exception("User not authenticated"))

            val docId = task.id.ifEmpty { UUID.randomUUID().toString() }
            firestore.collection("users")
                .document(uid)
                .collection("tasks")
                .document(docId)
                .set(task)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun deleteTask(taskId: String): Result<Unit> {
        return try {
            collection.document(taskId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
