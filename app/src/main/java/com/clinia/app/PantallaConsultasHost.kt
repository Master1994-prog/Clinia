/*package com.clinia.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.clinia.app.data.local.CliniaDatabase
import com.clinia.app.data.local.consultas.ConsultasRepository
import com.clinia.app.data.local.consultas.ConsultasViewModel
import com.clinia.app.data.local.consultas.ConsultasViewModelFactory

@Composable
fun PantallaConsultasHost(
    medicoId: Long,
    pacienteId: Long
) {
    val context = LocalContext.current.applicationContext

    // DB + Repo (igual que el resto del proyecto)
    val db = CliniaDatabase.get(context)
    val repository = ConsultasRepository(
        dao = db.consultasDao()
    )

    val viewModel: ConsultasViewModel = viewModel(
        factory = ConsultasViewModelFactory(repository)
    )

    val state by viewModel.state.collectAsState()

    // 🔹 Cargar consultas al entrar
    LaunchedEffect(medicoId, pacienteId) {
        if (medicoId > 0 && pacienteId > 0) {
            viewModel.cargarConsultas(
                medicoId = medicoId,
                pacienteId = pacienteId
            )
        }
    }

    // 🔹 UI principal
    CliniaConsultas(
        state = state,
        onCrearConsulta = { motivo ->
            viewModel.crearConsulta(
                medicoId = medicoId,
                pacienteId = pacienteId,
                motivo = motivo
            )
        }
    )
}*/

package com.clinia.app

import androidx.compose.runtime.Composable

@Composable
fun PantallaConsultasHost(
    medicoId: Long,
    pacienteId: Long
) {
    // ✅ Solución rápida: no dependemos de ViewModel/Factory aún
    // porque tu pantalla CliniaConsultas() actual no recibe state ni callbacks.
    CliniaConsultas()
}
