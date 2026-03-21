package mx.edu.itesca.alzheimer_app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.edu.itesca.alzheimer_app.database.DatabaseHelper
import mx.edu.itesca.alzheimer_app.model.Paciente

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetallePacienteScreen(
    db: DatabaseHelper,
    pacienteId: Int,
    onNuevaEvaluacion: (Int) -> Unit,
    onVerHistorial: (Int) -> Unit,
    onRegresar: () -> Unit
) {
    val pacientes = db.obtenerPacientes()
    val paciente = pacientes.find { it.id == pacienteId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Paciente") },
                navigationIcon = {
                    IconButton(onClick = onRegresar) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        if (paciente == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = androidx.compose.ui.Alignment.Center
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
                // Tarjeta con info del paciente
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

                Spacer(modifier = Modifier.height(8.dp))

                // Botones de accion
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
            }
        }
    }
}

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