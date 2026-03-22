package mx.edu.itesca.alzheimer_app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.edu.itesca.alzheimer_app.database.DatabaseHelper
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetallePacienteScreen(
    db: DatabaseHelper,
    pacienteId: Int,
    onNuevaEvaluacion: (Int) -> Unit,
    onVerHistorial: (Int) -> Unit,
    onVerProgreso: (Int) -> Unit,
    onRegresar: () -> Unit
) {
    val pacientes = db.obtenerPacientes()
    val paciente = pacientes.find { it.id == pacienteId }
    val instrumentos = listOf("MMSE", "Tinetti")

    Scaffold(
        topBar = {
            AppTopBar(
                titulo = "Detalle del Paciente",
                navigationIcon = {
                    IconButton(onClick = onRegresar) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
        }
    ) { padding ->
        if (paciente == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Paciente no encontrado")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Tarjeta info paciente
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "${paciente.nombre} ${paciente.apellido}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        HorizontalDivider()
                        FilaInfo(label = "Fecha de nacimiento", valor = paciente.fechaNacimiento)
                        FilaInfo(label = "Género", valor = paciente.genero)
                        FilaInfo(label = "Teléfono", valor = paciente.telefono.ifBlank { "No registrado" })
                    }
                }

                // Tarjeta de estado de instrumentos
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Estado de evaluaciones",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        HorizontalDivider()
                        instrumentos.forEach { instrumento ->
                            FilaInstrumento(
                                instrumento = instrumento,
                                db = db,
                                pacienteId = pacienteId
                            )
                        }
                    }
                }

                // Botones
                Button(
                    onClick = { onNuevaEvaluacion(pacienteId) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Nueva evaluación")
                }

                OutlinedButton(
                    onClick = { onVerHistorial(pacienteId) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Ver historial de evaluaciones")
                }

                OutlinedButton(
                    onClick = { onVerProgreso(pacienteId) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Ver progreso")
                }
            }
        }
    }
}

@Composable
fun FilaInstrumento(
    instrumento: String,
    db: DatabaseHelper,
    pacienteId: Int
) {
    val ultimaEval = remember { db.obtenerUltimaEvaluacion(pacienteId, instrumento) }
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    val (estado, colorEstado, proximaFecha) = remember(ultimaEval) {
        if (ultimaEval == null) {
            Triple("Pendiente", EstadoColor.ROJO, null)
        } else {
            val fechaUltima = sdf.parse(ultimaEval.fecha)
            val hoy = Date()
            val cal = Calendar.getInstance()
            cal.time = fechaUltima ?: hoy
            cal.add(Calendar.MONTH, 4)
            val proxima = cal.time
            val diasRestantes = ((proxima.time - hoy.time) / (1000 * 60 * 60 * 24)).toInt()

            when {
                diasRestantes < 0 -> Triple("Vencida", EstadoColor.ROJO, sdf.format(proxima))
                diasRestantes <= 30 -> Triple("Próxima en $diasRestantes días", EstadoColor.AMARILLO, sdf.format(proxima))
                else -> Triple("Al día", EstadoColor.VERDE, sdf.format(proxima))
            }
        }
    }

    val color = when (colorEstado) {
        EstadoColor.VERDE -> MaterialTheme.colorScheme.primary
        EstadoColor.AMARILLO -> MaterialTheme.colorScheme.tertiary
        EstadoColor.ROJO -> MaterialTheme.colorScheme.error
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = instrumento,
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp
            )
            if (ultimaEval != null) {
                Text(
                    text = "Última: ${ultimaEval.fecha}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                proximaFecha?.let {
                    Text(
                        text = "Próxima: $it",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        Text(
            text = estado,
            color = color,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
        )
    }
}

enum class EstadoColor { VERDE, AMARILLO, ROJO }

@Composable
fun FilaInfo(label: String, valor: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp
        )
        Text(
            text = valor,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp
        )
    }
}