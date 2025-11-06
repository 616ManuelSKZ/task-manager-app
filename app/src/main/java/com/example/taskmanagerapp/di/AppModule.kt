package com.example.taskmanagerapp.di

import android.content.Context
import androidx.room.Room
import com.example.taskmanagerapp.data.local.dao.CategoryDao
import com.example.taskmanagerapp.data.local.dao.TaskDao
import com.example.taskmanagerapp.data.local.db.TaskDatabase
import com.example.taskmanagerapp.data.remote.firebase.CategoryFirebaseSource
import com.example.taskmanagerapp.data.remote.firebase.FirebaseDataSource
import com.example.taskmanagerapp.data.remote.firebase.FirebaseAuthSource
import com.example.taskmanagerapp.data.repository.AuthRepository
import com.example.taskmanagerapp.data.repository.AuthRepositoryImpl
import com.example.taskmanagerapp.data.repository.CategoryRepository
import com.example.taskmanagerapp.data.repository.CategoryRepositoryImpl
import com.example.taskmanagerapp.data.repository.TaskRepository
import com.example.taskmanagerapp.data.repository.TaskRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // 🔹 Firebase
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseAuthSource(auth: FirebaseAuth): FirebaseAuthSource =
        FirebaseAuthSource(auth)

    @Provides
    @Singleton
    fun provideFirebaseDataSource(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore
    ): FirebaseDataSource = FirebaseDataSource(auth, firestore)

    // 🔹 Room Database
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): TaskDatabase =
        Room.databaseBuilder(
            context,
            TaskDatabase::class.java,
            "tasks_db"
        ).build()

    @Provides
    fun provideTaskDao(database: TaskDatabase): TaskDao = database.taskDao()

    // 🔹 Repositories
    @Provides
    @Singleton
    fun provideTaskRepository(
        taskDao: TaskDao,
        firebaseDataSource: FirebaseDataSource
    ): TaskRepository = TaskRepositoryImpl(taskDao, firebaseDataSource)

    @Provides
    @Singleton
    fun provideAuthRepository(authSource: FirebaseAuthSource): AuthRepository =
        AuthRepositoryImpl(authSource)

    @Provides
    @Singleton
    fun provideCategoryRepository(
        dao: CategoryDao,
        remote: CategoryFirebaseSource
    ): CategoryRepository = CategoryRepositoryImpl(dao, remote)

    @Provides
    fun provideCategoryDao(db: TaskDatabase): CategoryDao = db.categoryDao()

    @Provides
    @Singleton
    fun provideCategoryFirebaseSource(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore
    ): CategoryFirebaseSource = CategoryFirebaseSource(auth, firestore)

}
