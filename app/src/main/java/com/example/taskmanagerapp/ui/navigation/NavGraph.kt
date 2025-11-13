package com.example.taskmanagerapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.taskmanagerapp.data.local.entity.TaskEntity
import com.example.taskmanagerapp.ui.components.MainScaffold
import com.example.taskmanagerapp.ui.screen.auth.LoginScreen
import com.example.taskmanagerapp.ui.screen.auth.RegisterScreen
import com.example.taskmanagerapp.ui.screen.categories.CategoryListScreen
import com.example.taskmanagerapp.ui.screen.profile.ProfileScreen
import com.example.taskmanagerapp.ui.screen.tasks.TaskDetailScreen
import com.example.taskmanagerapp.ui.screen.tasks.TaskEditScreen
import com.example.taskmanagerapp.ui.screen.tasks.TaskListScreen
import com.example.taskmanagerapp.viewmodel.AuthViewModel
import com.google.gson.Gson

@Composable
fun NavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val authState by authViewModel.authState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = if (authState.user != null) "tasks" else "login"
    ) {

        // ------------------ AUTH ------------------
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("tasks") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate("register") }
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

        // ------------------ MAIN SCREENS (con BottomBar) ------------------
        composable("tasks") {
            MainScaffold(navController) {
                TaskListScreen(
                    onLogout = { authViewModel.logout() },
                    onTaskSelected = { selectedTask ->
                        val json = Gson().toJson(selectedTask)
                        navController.navigate("taskDetail/$json")
                    },
                    onNavigateToCategories = {
                        navController.navigate("categories")
                    },
                    onAddTask = {
                        navController.navigate("taskEdit")
                    }
                )
            }
        }

        composable("categories") {
            MainScaffold(navController) {
                CategoryListScreen(onBack = { navController.popBackStack() })
            }
        }

        composable("profile") {
            MainScaffold(navController) {
                ProfileScreen()
            }
        }

        // ------------------ SCREENS SIN BottomBar ------------------
        composable(
            route = "taskDetail/{taskJson}",
            arguments = listOf(navArgument("taskJson") { type = NavType.StringType })
        ) { entry ->
            val task = Gson().fromJson(entry.arguments?.getString("taskJson"), TaskEntity::class.java)

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
    }
}
