package mx.edu.itesca.alzheimer_app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.edu.itesca.alzheimer_app.database.DatabaseHelper
import mx.edu.itesca.alzheimer_app.model.Paciente

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaPacientesScreen(
    db: DatabaseHelper,
    onAgregarPaciente: () -> Unit,
    onVerDetalle: (Int) -> Unit
) {
    var pacientes by remember { mutableStateOf(db.obtenerPacientes()) }
    var pacienteAEliminar by remember { mutableStateOf<Paciente?>(null) }

    // Dialogo de confirmacion para eliminar
    pacienteAEliminar?.let { paciente ->
        AlertDialog(
            onDismissRequest = { pacienteAEliminar = null },
            title = { Text("Eliminar paciente") },
            text = { Text("¿Estás seguro de eliminar a ${paciente.nombre} ${paciente.apellido}?") },
            confirmButton = {
                TextButton(onClick = {
                    db.eliminarPaciente(paciente.id)
                    pacientes = db.obtenerPacientes()
                    pacienteAEliminar = null
                }) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { pacienteAEliminar = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            AppTopBar(titulo = "Estancia Alzheimer Obregón")
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAgregarPaciente) {
                Icon(Icons.Default.Add, contentDescription = "Agregar paciente")
            }
        }
    ) { padding ->
        if (pacientes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No hay pacientes registrados",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(pacientes) { paciente ->
                    CardPaciente(
                        paciente = paciente,
                        onClick = { onVerDetalle(paciente.id) },
                        onEliminar = { pacienteAEliminar = paciente }
                    )
                }
            }
        }
    }
}

@Composable
fun CardPaciente(
    paciente: Paciente,
    onClick: () -> Unit,
    onEliminar: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${paciente.nombre} ${paciente.apellido}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Nacimiento: ${paciente.fechaNacimiento}",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = paciente.genero,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onEliminar) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}