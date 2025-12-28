package com.clinia.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun CliniaMenuShell(
    doctorNombre: String,
    onLogout: () -> Unit
) {
    val tabNav = rememberNavController()

    Scaffold(
        containerColor = Color(0xFFF4F7FB),
        bottomBar = { CliniaBottomBar(navController = tabNav) }
    ) { _ ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF4F7FB))
        ) {
            NavHost(
                navController = tabNav,
                startDestination = CliniaTabRoutes.INICIO
            ) {
                composable(CliniaTabRoutes.INICIO) {
                    CliniaHome(doctorNombre = doctorNombre)
                }
                composable(CliniaTabRoutes.CHATBOX) {
                    CliniaChatboxScreen()
                }
                composable(CliniaTabRoutes.PACIENTES) {
                    CliniaPacientes()
                }
                composable(CliniaTabRoutes.CONSULTAS) {
                    CliniaConsultas()
                }
                composable(CliniaTabRoutes.AJUSTES) {
                    CliniaAjustes(onLogout = onLogout)
                }
            }
        }
    }
}
