package com.clinia.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun CliniaMenuShell(
    doctorNombre: String,
    onLogout: () -> Unit
) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { CliniaBottomBar(navController) }
    ) { padding ->
        Box(Modifier.padding(padding)) {
            NavHost(navController, startDestination = CliniaTabRoutes.INICIO) {
                composable(CliniaTabRoutes.INICIO) { CliniaHome(doctorNombre) }
                composable(CliniaTabRoutes.CHATBOX) { CliniaChatboxScreen() }
                composable(CliniaTabRoutes.PACIENTES) { CliniaPacientes() }
                composable(CliniaTabRoutes.CONSULTAS) { CliniaConsultas() }
                composable(CliniaTabRoutes.AJUSTES) {
                    CliniaAjustes(onLogout = onLogout) // ✅ logout directo
                }
            }
        }
    }
}
