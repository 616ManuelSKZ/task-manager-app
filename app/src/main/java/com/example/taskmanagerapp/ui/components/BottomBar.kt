package com.example.taskmanagerapp.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun AppBottomBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.primary) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Tareas") },
            label = { Text("Tareas") },
            selected = currentRoute == "tasks",
            onClick = { onNavigate("tasks") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                unselectedIconColor = Color.LightGray,
                indicatorColor = MaterialTheme.colorScheme.primary
            )
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.List, contentDescription = "Categorías") },
            label = { Text("Categorías") },
            selected = currentRoute == "categories",
            onClick = { onNavigate("categories") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                unselectedIconColor = Color.LightGray,
                indicatorColor = MaterialTheme.colorScheme.primary
            )
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
            label = { Text("Perfil") },
            selected = currentRoute == "profile",
            onClick = { onNavigate("profile") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                unselectedIconColor = Color.LightGray,
                indicatorColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}
