package mx.edu.itesca.alzheimer_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import mx.edu.itesca.alzheimer_app.database.DatabaseHelper
import mx.edu.itesca.alzheimer_app.screens.*
import mx.edu.itesca.alzheimer_app.ui.theme.AlzheimerappTheme
import androidx.activity.compose.BackHandler
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = DatabaseHelper(this)

        setContent {
            AlzheimerappTheme {
                var currentScreen by remember { mutableStateOf<Screen>(Screen.ListaPacientes) }

                BackHandler(enabled = currentScreen != Screen.ListaPacientes) {
                    currentScreen = when (val screen = currentScreen) {
                        is Screen.AltaPaciente -> Screen.ListaPacientes
                        is Screen.DetallePaciente -> Screen.ListaPacientes
                        is Screen.NuevaEvaluacion -> Screen.DetallePaciente(screen.pacienteId)
                        is Screen.Historial -> Screen.DetallePaciente(screen.pacienteId)
                        else -> Screen.ListaPacientes
                    }
                }

                when (val screen = currentScreen) {
                    is Screen.ListaPacientes -> ListaPacientesScreen(
                        db = db,
                        onAgregarPaciente = { currentScreen = Screen.AltaPaciente },
                        onVerDetalle = { id -> currentScreen = Screen.DetallePaciente(id) }
                    )
                    is Screen.AltaPaciente -> AltaPacienteScreen(
                        db = db,
                        onGuardar = { currentScreen = Screen.ListaPacientes },
                        onCancelar = { currentScreen = Screen.ListaPacientes }
                    )
                    is Screen.DetallePaciente -> DetallePacienteScreen(
                        db = db,
                        pacienteId = screen.pacienteId,
                        onNuevaEvaluacion = { id -> currentScreen = Screen.NuevaEvaluacion(id) },
                        onVerHistorial = { id -> currentScreen = Screen.Historial(id) },
                        onRegresar = { currentScreen = Screen.ListaPacientes }
                    )
                    is Screen.NuevaEvaluacion -> NuevaEvaluacionScreen(
                        db = db,
                        pacienteId = screen.pacienteId,
                        onGuardar = { currentScreen = Screen.DetallePaciente(screen.pacienteId) },
                        onCancelar = { currentScreen = Screen.DetallePaciente(screen.pacienteId) }
                    )
                    is Screen.Historial -> HistorialScreen(
                        db = db,
                        pacienteId = screen.pacienteId,
                        onRegresar = { currentScreen = Screen.DetallePaciente(screen.pacienteId) }
                    )
                }
            }
        }
    }
}