package com.example.makeupstoreapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.makeupstoreapp.ui.screens.LoginScreen
import com.example.makeupstoreapp.ui.screens.RegisterScreen
import com.example.makeupstoreapp.viewmodel.AuthViewModel
import com.example.makeupstoreapp.ui.screens.HomeScreen
import com.example.makeupstoreapp.viewmodel.ThemeViewModel
import com.example.makeupstoreapp.ui.theme.MakeupStoreAppTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()
            val authViewModel: AuthViewModel = viewModel()
            val themeViewModel: ThemeViewModel = viewModel()
            val isDarkMode by themeViewModel.isDarkMode.collectAsState()

            MakeupStoreAppTheme(
                darkTheme = isDarkMode
            ) {

                NavHost(
                    navController = navController,
                    startDestination = "login"
                ) {
                    composable("login") {
                        LoginScreen(
                            authViewModel = authViewModel,
                            onLoginSuccess = {
                                navController.navigate("home")
                            },
                            onGoToRegister = {
                                navController.navigate("register")
                            },
                            isDarkMode = isDarkMode
                        )
                    }

                    composable("register") {
                        RegisterScreen(
                            authViewModel = authViewModel,
                            onRegisterSuccess = {
                                navController.navigate("login")
                            },
                            onGoToLogin = {
                                navController.navigate("login")
                            },
                            isDarkMode = isDarkMode
                        )
                    }

                    composable("home") {
                        HomeScreen(
                            onLogout = {
                                authViewModel.logout()
                                navController.navigate("login")
                            },
                            themeViewModel = themeViewModel
                        )
                    }
                }
            }
        }
    }
}