package com.example.taskmanagerapp.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.taskmanagerapp.data.local.entity.TaskEntity
import com.example.taskmanagerapp.ui.navigation.NavGraph
import com.example.taskmanagerapp.ui.screen.auth.LoginScreen
import com.example.taskmanagerapp.ui.screen.auth.RegisterScreen
import com.example.taskmanagerapp.ui.screen.categories.CategoryListScreen
import com.example.taskmanagerapp.ui.screen.tasks.TaskDetailScreen
import com.example.taskmanagerapp.ui.screen.tasks.TaskEditScreen
import com.example.taskmanagerapp.ui.screen.tasks.TaskListScreen
import com.example.taskmanagerapp.ui.theme.TaskManagerTheme
import com.example.taskmanagerapp.viewmodel.AuthViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val authViewModel: AuthViewModel = hiltViewModel()

            TaskManagerTheme {
                NavGraph(navController = navController, authViewModel = authViewModel)
            }
        }
    }
}


@Composable
fun TaskManagerApp() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.authState.collectAsState()

    TaskManagerTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            when {
                authState.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                authState.user != null -> {
                    // 🔹 Usuario autenticado → flujo principal
                    NavHost(
                        navController = navController,
                        startDestination = "tasks"
                    ) {
                        composable("tasks") {
                            TaskListScreen(
                                onLogout = { authViewModel.logout() },
                                onTaskSelected = { selectedTask ->
                                    val json = Gson().toJson(selectedTask)
                                    navController.navigate("taskDetail/$json")
                                },
                                onNavigateToCategories = {
                                    navController.navigate("categories")
                                }
                            )
                        }

                        composable(
                            route = "taskDetail/{taskJson}",
                            arguments = listOf(navArgument("taskJson") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val taskJson = backStackEntry.arguments?.getString("taskJson")
                            val task = Gson().fromJson(taskJson, TaskEntity::class.java)
                            TaskDetailScreen(
                                task = task,
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable("taskEdit") {
                            TaskEditScreen(
                                onSave = { navController.popBackStack() },
                                onCancel = { navController.popBackStack() }
                            )
                        }

                        composable("categories") {
                            CategoryListScreen(onBack = { navController.popBackStack() })
                        }
                    }
                }

                else -> {
                    // 🔹 Usuario no autenticado → flujo de login/registro
                    NavHost(navController = navController, startDestination = "login") {
                        composable("login") {
                            LoginScreen(
                                onLoginSuccess = {
                                    navController.navigate("tasks") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                },
                                onNavigateToRegister = {
                                    navController.navigate("register")
                                }
                            )
                        }

                        composable("register") {
                            RegisterScreen(
                                onRegisterSuccess = {
                                    navController.navigate("tasks") {
                                        popUpTo("register") { inclusive = true }
                                    }
                                },
                                onNavigateToLogin = {
                                    navController.navigate("login") {
                                        popUpTo("register") { inclusive = true }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
