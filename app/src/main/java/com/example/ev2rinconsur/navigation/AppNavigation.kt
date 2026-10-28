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
                        launchSingleTop = true
                    }
                },
                onGoRegister = {
                    authViewModel.clearLoginResult()
                    navController.navigate(Route.Register.path) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Route.Register.path) {
            RegisterScreenVm(
                vm = authViewModel,
                onRegisteredNavigateLogin = {
                    authViewModel.clearLoginResult()
                    navController.popBackStack()
                },
                onGoLogin = {
                    authViewModel.clearRegisterResult()
                    authViewModel.clearLoginResult()
                    navController.popBackStack()
                }
            )
        }

        composable(Route.Home.path) {
            HomeScreen(
                authViewModel = authViewModel,
                conteoViewModel = conteoViewModel,
                onGoProfile = {
                    navController.navigate(Route.Profile.path) {
                        launchSingleTop = true
                    }
                },
                onGoCrud = {
                    navController.navigate(Route.Crud.path) {
                        launchSingleTop = true
                    }
                },
                onGoTotals = {
                    navController.navigate(Route.Totals.path) {
                        launchSingleTop = true
                    }
                },
                onGoHistory = {
                    conteoViewModel.abrirHistorialTurnosAnteriores()
                    navController.navigate(Route.History.path) {
                        launchSingleTop = true
                    }
                }
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
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Route.Crud.path) {
            ConteoCrudScreen(
                vm = conteoViewModel,
                onBack = { navController.popBackStack() },
                onFinishTurno = {
                    navController.navigate(Route.History.path) {
                        popUpTo(Route.Home.path)
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Route.Totals.path) {
            TotalesScreen(
                vm = conteoViewModel,
                onBack = { navController.popBackStack() },
                onGoHistory = {
                    conteoViewModel.abrirRegistrosDelDia()
                    navController.navigate(Route.History.path) {
                        launchSingleTop = true
                    }
                },
                onGoCrud = {
                    navController.navigate(Route.Crud.path) {
                        launchSingleTop = true
                    }
                }
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
