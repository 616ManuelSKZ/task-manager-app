package com.example.taskmanagerapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.taskmanagerapp.ui.screen.auth.LoginScreen
import com.example.taskmanagerapp.ui.screen.auth.RegisterScreen

@Composable
fun AuthNavGraph(
    navController: NavHostController,
    onAuthSuccess: () -> Unit
) {
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                onNavigateToRegister = { navController.navigate("register") },
                onLoginSuccess = onAuthSuccess
            )
        }
        composable("register") {
            RegisterScreen(
                onNavigateToLogin = { navController.popBackStack() },
                onRegisterSuccess = onAuthSuccess
            )
        }
    }
}
