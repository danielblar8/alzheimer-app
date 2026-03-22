package mx.edu.itesca.alzheimer_app.screens

sealed class Screen {
    object ListaPacientes : Screen()
    object AltaPaciente : Screen()
    data class DetallePaciente(val pacienteId: Int) : Screen()
    data class NuevaEvaluacion(val pacienteId: Int) : Screen()
    data class Historial(val pacienteId: Int) : Screen()

    data class Progreso(val pacienteId: Int) : Screen()


}