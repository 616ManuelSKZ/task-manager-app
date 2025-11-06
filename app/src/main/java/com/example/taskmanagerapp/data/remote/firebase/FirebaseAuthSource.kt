package com.example.taskmanagerapp.data.remote.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {
    val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    fun getAuthState(): Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser)
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }

    suspend fun login(email: String, password: String): Result<FirebaseUser?> {
        return try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Result.success(firebaseAuth.currentUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(email: String, password: String): Result<FirebaseUser?> {
        return try {
            firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            Result.success(firebaseAuth.currentUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        firebaseAuth.signOut()
    }
}
