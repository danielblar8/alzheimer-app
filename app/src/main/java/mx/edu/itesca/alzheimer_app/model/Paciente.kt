package mx.edu.itesca.alzheimer_app.model

data class Paciente(
    val id: Int = 0,
    val nombre: String,
    val apellido: String,
    val fechaNacimiento: String,
    val genero: String,
    val telefono: String
)

