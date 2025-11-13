package com.example.taskmanagerapp.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun MainScaffold(
    navController: NavHostController,
    content: @Composable () -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "tasks"

    Scaffold(
        topBar = {
            AppTopBar(
                title = getTitleForRoute(currentRoute),
                onBack = if (currentRoute.contains("detail") || currentRoute.contains("edit"))
                { { navController.popBackStack() } }
                else null
            )
        },
        bottomBar = {
            if (showBottomBar(currentRoute)) {
                AppBottomBar(
                    currentRoute = currentRoute,
                    onNavigate = { navController.navigate(it) }
                )
            }
        }
    ) { padding ->
        Surface(modifier = Modifier.padding(padding)) {
            content()
        }
    }
}

private fun showBottomBar(route: String): Boolean {
    return route in listOf("tasks", "categories", "profile")
}

private fun getTitleForRoute(route: String): String {
    return when {
        route == "tasks" -> "Mis tareas"
        route == "categories" -> "Categorías"
        route == "profile" -> "Perfil"
        route.contains("edit") -> "Editar tarea"
        route.contains("detail") -> "Detalle"
        else -> ""
    }
}
