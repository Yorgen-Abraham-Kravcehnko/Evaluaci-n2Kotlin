package com.example.ev2rinconsur.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.ev2rinconsur.ui.screen.ConteoCrudScreen
import com.example.ev2rinconsur.ui.screen.HistorialScreen
import com.example.ev2rinconsur.ui.screen.HomeScreen
import com.example.ev2rinconsur.ui.screen.LoginScreenVm
import com.example.ev2rinconsur.ui.screen.ProfileScreen
import com.example.ev2rinconsur.ui.screen.RegisterScreenVm
import com.example.ev2rinconsur.ui.screen.TotalesScreen
import com.example.ev2rinconsur.ui.viewmodel.AuthViewModel
import com.example.ev2rinconsur.ui.viewmodel.ConteoViewModel

@Composable
fun AppNavigation(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    conteoViewModel: ConteoViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Route.Login.path
    ) {
        composable(Route.Login.path) {
            LoginScreenVm(
                vm = authViewModel,
                onLoginOkNavigateHome = {
                    navController.navigate(Route.Home.path) {
                        popUpTo(Route.Login.path) { inclusive = true }
                    }
                },
                onGoRegister = { navController.navigate(Route.Register.path) }
            )
        }

        composable(Route.Register.path) {
            RegisterScreenVm(
                vm = authViewModel,
                onRegisteredNavigateLogin = { navController.popBackStack() },
                onGoLogin = { navController.popBackStack() }
            )
        }

        composable(Route.Home.path) {
            HomeScreen(
                authViewModel = authViewModel,
                conteoViewModel = conteoViewModel,
                onGoProfile = { navController.navigate(Route.Profile.path) },
                onGoCrud = { navController.navigate(Route.Crud.path) },
                onGoTotals = { navController.navigate(Route.Totals.path) },
                onGoHistory = { navController.navigate(Route.History.path) }
            )
        }

        composable(Route.Profile.path) {
            ProfileScreen(
                authViewModel = authViewModel,
                conteoViewModel = conteoViewModel,
                onBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate(Route.Login.path) {
                        popUpTo(Route.Home.path) { inclusive = true }
                    }
                }
            )
        }

        composable(Route.Crud.path) {
            ConteoCrudScreen(
                vm = conteoViewModel,
                onBack = { navController.popBackStack() },
                onFinishTurno = {
                    navController.navigate(Route.Totals.path) {
                        popUpTo(Route.Home.path)
                    }
                }
            )
        }

        composable(Route.Totals.path) {
            TotalesScreen(
                vm = conteoViewModel,
                onBack = { navController.popBackStack() },
                onGoHistory = { navController.navigate(Route.History.path) },
                onGoCrud = { navController.navigate(Route.Crud.path) }
            )
        }

        composable(Route.History.path) {
            HistorialScreen(
                vm = conteoViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
