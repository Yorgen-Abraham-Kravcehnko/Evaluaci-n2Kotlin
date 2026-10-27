package com.example.ev2rinconsur

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.ev2rinconsur.data.local.database.AppDatabase
import com.example.ev2rinconsur.data.repository.RegistroRepository
import com.example.ev2rinconsur.data.repository.UserRepository
import com.example.ev2rinconsur.navigation.AppNavigation
import com.example.ev2rinconsur.ui.theme.EV2RinconSurTheme
import com.example.ev2rinconsur.ui.viewmodel.AuthViewModel
import com.example.ev2rinconsur.ui.viewmodel.AuthViewModelFactory
import com.example.ev2rinconsur.ui.viewmodel.ConteoViewModel
import com.example.ev2rinconsur.ui.viewmodel.ConteoViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = AppDatabase.getInstance(applicationContext)
        val userRepository = UserRepository(db.userDao())
        val registroRepository = RegistroRepository(db.registroAccesoDao())

        setContent {
            EV2RinconSurTheme {
                val navController = rememberNavController()
                val authViewModel: AuthViewModel = viewModel(
                    factory = AuthViewModelFactory(userRepository)
                )
                val conteoViewModel: ConteoViewModel = viewModel(
                    factory = ConteoViewModelFactory(registroRepository)
                )

                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavigation(
                        navController = navController,
                        authViewModel = authViewModel,
                        conteoViewModel = conteoViewModel
                    )
                }
            }
        }
    }
}
