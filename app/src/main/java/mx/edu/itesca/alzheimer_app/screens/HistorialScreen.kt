package mx.edu.itesca.alzheimer_app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.edu.itesca.alzheimer_app.database.DatabaseHelper
import mx.edu.itesca.alzheimer_app.model.Evaluacion

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialScreen(
    db: DatabaseHelper,
    pacienteId: Int,
    onRegresar: () -> Unit
) {
    val evaluaciones = remember { db.obtenerEvaluacionesPorPaciente(pacienteId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial de Evaluaciones") },
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
        if (evaluaciones.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No hay evaluaciones registradas",
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
                items(evaluaciones) { evaluacion ->
                    CardEvaluacion(evaluacion = evaluacion)
                }
            }
        }
    }
}

@Composable
fun CardEvaluacion(evaluacion: Evaluacion) {
    val (color, estado) = when (evaluacion.instrumento) {
        "MMSE" -> when {
            evaluacion.puntaje >= 24 -> Pair(MaterialTheme.colorScheme.primary, "Normal")
            evaluacion.puntaje >= 18 -> Pair(MaterialTheme.colorScheme.tertiary, "Deterioro leve")
            else -> Pair(MaterialTheme.colorScheme.error, "Deterioro grave")
        }
        "Tinetti" -> when {
            evaluacion.puntaje >= 26 -> Pair(MaterialTheme.colorScheme.primary, "Normal")
            evaluacion.puntaje >= 19 -> Pair(MaterialTheme.colorScheme.tertiary, "Riesgo de caída")
            else -> Pair(MaterialTheme.colorScheme.error, "Alto riesgo de caída")
        }
        else -> Pair(MaterialTheme.colorScheme.primary, "")
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = evaluacion.instrumento,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = evaluacion.fecha,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            HorizontalDivider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Puntaje: ${evaluacion.puntaje} / ${evaluacion.puntajeMaximo}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = estado,
                    color = color,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }

            if (evaluacion.observaciones.isNotBlank()) {
                Text(
                    text = "Observaciones: ${evaluacion.observaciones}",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}