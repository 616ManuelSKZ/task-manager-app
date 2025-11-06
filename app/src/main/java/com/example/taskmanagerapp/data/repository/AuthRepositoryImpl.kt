package com.example.taskmanagerapp.data.repository

import com.example.taskmanagerapp.data.remote.firebase.FirebaseAuthSource
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuthSource: FirebaseAuthSource
) : AuthRepository {

    override val currentUser: FirebaseUser?
        get() = firebaseAuthSource.currentUser

    override fun getAuthState(): Flow<FirebaseUser?> = firebaseAuthSource.getAuthState()

    override suspend fun login(email: String, password: String): Result<FirebaseUser?> =
        firebaseAuthSource.login(email, password)

    override suspend fun register(email: String, password: String): Result<FirebaseUser?> =
        firebaseAuthSource.register(email, password)

    override fun logout() = firebaseAuthSource.logout()
}
