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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.example.ev2rinconsur.util.Vibrador
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ev2rinconsur.ui.viewmodel.SessionStatus

@Composable
fun AppNavigation(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    conteoViewModel: ConteoViewModel,
    vibrador: Vibrador
) {
    val session by authViewModel.session.collectAsStateWithLifecycle()

    if (session == SessionStatus.CHECKING) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val startDestination = remember {
        if (session == SessionStatus.LOGGED_IN) Route.Home.path else Route.Login.path
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
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
                    vibrador.inicioTurno()
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
                    authViewModel.logout {
                        navController.navigate(Route.Login.path) {
                            popUpTo(navController.graph.id) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }
            )
        }

        composable(Route.Crud.path) {
            ConteoCrudScreen(
                vm = conteoViewModel,
                onBack = { navController.popBackStack() },
                onFinishTurno = {
                    vibrador.finTurno()
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
