package mx.edu.itesca.alzheimer_app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.MenuAnchorType
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mx.edu.itesca.alzheimer_app.database.DatabaseHelper
import mx.edu.itesca.alzheimer_app.model.Evaluacion
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevaEvaluacionScreen(
    db: DatabaseHelper,
    pacienteId: Int,
    onGuardar: () -> Unit,
    onCancelar: () -> Unit
) {
    var instrumentoSeleccionado by remember { mutableStateOf("MMSE") }
    var expandedInstrumento by remember { mutableStateOf(false) }
    var puntaje by remember { mutableStateOf("") }
    var observaciones by remember { mutableStateOf("") }
    var errorMensaje by remember { mutableStateOf("") }

    val instrumentos = listOf("MMSE", "Tinetti")

    val puntajeMaximo = when (instrumentoSeleccionado) {
        "MMSE" -> 30
        "Tinetti" -> 28
        else -> 0
    }

    val descripcionInstrumento = when (instrumentoSeleccionado) {
        "MMSE" -> "Mini-Mental State Examination\nPuntaje máximo: 30\n• 24-30: Normal\n• 18-23: Deterioro leve\n• 0-17: Deterioro grave"
        "Tinetti" -> "Escala de Tinetti (equilibrio y marcha)\nPuntaje máximo: 28\n• 26-28: Normal\n• 19-25: Riesgo de caída\n• <19: Alto riesgo de caída"
        else -> ""
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva Evaluación") },
                navigationIcon = {
                    IconButton(onClick = onCancelar) {
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Dropdown instrumento
            ExposedDropdownMenuBox(
                expanded = expandedInstrumento,
                onExpandedChange = { expandedInstrumento = it }
            ) {
                OutlinedTextField(
                    value = instrumentoSeleccionado,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Instrumento") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedInstrumento)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                )
                ExposedDropdownMenu(
                    expanded = expandedInstrumento,
                    onDismissRequest = { expandedInstrumento = false }
                ) {
                    instrumentos.forEach { instrumento ->
                        DropdownMenuItem(
                            text = { Text(instrumento) },
                            onClick = {
                                instrumentoSeleccionado = instrumento
                                puntaje = ""
                                expandedInstrumento = false
                            }
                        )
                    }
                }
            }

            // Descripcion del instrumento
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Text(
                    text = descripcionInstrumento,
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            // Puntaje
            OutlinedTextField(
                value = puntaje,
                onValueChange = { valor ->
                    if (valor.all { it.isDigit() }) puntaje = valor
                },
                label = { Text("Puntaje obtenido (máximo $puntajeMaximo)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Observaciones
            OutlinedTextField(
                value = observaciones,
                onValueChange = { observaciones = it },
                label = { Text("Observaciones (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )

            if (errorMensaje.isNotEmpty()) {
                Text(
                    text = errorMensaje,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    val puntajeInt = puntaje.toIntOrNull()
                    when {
                        puntaje.isBlank() -> errorMensaje = "Ingresa el puntaje obtenido"
                        puntajeInt == null -> errorMensaje = "El puntaje debe ser un número"
                        puntajeInt > puntajeMaximo -> errorMensaje = "El puntaje no puede ser mayor a $puntajeMaximo"
                        puntajeInt < 0 -> errorMensaje = "El puntaje no puede ser negativo"
                        else -> {
                            val fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                .format(Date())
                            db.insertarEvaluacion(
                                Evaluacion(
                                    pacienteId = pacienteId,
                                    instrumento = instrumentoSeleccionado,
                                    puntaje = puntajeInt,
                                    puntajeMaximo = puntajeMaximo,
                                    fecha = fecha,
                                    observaciones = observaciones.trim()
                                )
                            )
                            onGuardar()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar evaluación")
            }

            OutlinedButton(
                onClick = onCancelar,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancelar")
            }
        }
    }
}