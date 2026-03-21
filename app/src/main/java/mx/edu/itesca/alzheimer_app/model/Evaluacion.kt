package mx.edu.itesca.alzheimer_app.model

data class Evaluacion(
    val id: Int = 0,
    val pacienteId: Int,
    val instrumento: String,
    val puntaje: Int,
    val puntajeMaximo: Int,
    val fecha: String,
    val observaciones: String = ""
)