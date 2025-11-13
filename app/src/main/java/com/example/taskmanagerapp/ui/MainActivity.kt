package com.example.taskmanagerapp.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.example.taskmanagerapp.ui.navigation.NavGraph
import com.example.taskmanagerapp.ui.theme.TaskManagerTheme
import com.example.taskmanagerapp.viewmodel.AuthViewModel
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