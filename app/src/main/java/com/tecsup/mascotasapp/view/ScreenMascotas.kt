package com.tecsup.mascotasapp.view

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.tecsup.mascotasapp.data.MascotaModel
import com.tecsup.mascotasapp.data.RetrofitClient
import kotlinx.coroutines.launch

// Pantalla para listar y registrar mascotas
@Composable
fun ScreenMascotas() {

    val scope = rememberCoroutineScope()

    // Lista de mascotas obtenida desde Django
    var mascotas by remember {
        mutableStateOf<List<MascotaModel>>(emptyList())
    }

    var cargando by remember { mutableStateOf(true) }
    var guardando by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }

    // Campos del formulario
    var nombre by remember { mutableStateOf("") }
    var especie by remember { mutableStateOf("") }
    var raza by remember { mutableStateOf("") }
    var edad by remember { mutableStateOf("") }

    // Carga inicial de mascotas
    LaunchedEffect(Unit) {
        try {
            mascotas = RetrofitClient.servicio.listarMascotas()
            error = ""
        } catch (e: Exception) {
            Log.e("MASCOTAS_API", "Error al cargar mascotas", e)
            error = "Error al cargar mascotas: ${e.message}"
        } finally {
            cargando = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Mis Mascotas 🐾",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Formulario para registrar mascotas
        Text(
            text = "Registrar nueva mascota",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = especie,
            onValueChange = { especie = it },
            label = { Text("Especie") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = raza,
            onValueChange = { raza = it },
            label = { Text("Raza") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = edad,
            onValueChange = { edad = it },
            label = { Text("Edad") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                val edadNumero = edad.toIntOrNull()

                if (
                    nombre.isBlank() ||
                    especie.isBlank() ||
                    raza.isBlank() ||
                    edadNumero == null ||
                    edadNumero < 0
                ) {
                    mensaje = ""
                    error = "Completa todos los campos e ingresa una edad válida"
                } else {
                    scope.launch {
                        guardando = true
                        error = ""
                        mensaje = ""

                        try {
                            val nuevaMascota = MascotaModel(
                                id = 0,
                                nombre = nombre.trim(),
                                especie = especie.trim(),
                                raza = raza.trim(),
                                edad = edadNumero
                            )

                            // Envía la mascota a Django mediante POST
                            RetrofitClient.servicio.crearMascota(
                                nuevaMascota
                            )

                            // Actualiza la lista después del registro
                            mascotas = RetrofitClient.servicio
                                .listarMascotas()

                            // Limpia el formulario
                            nombre = ""
                            especie = ""
                            raza = ""
                            edad = ""

                            mensaje = "Mascota registrada correctamente"

                        } catch (e: Exception) {
                            Log.e(
                                "MASCOTAS_API",
                                "Error al registrar mascota",
                                e
                            )
                            error = "No se pudo registrar: ${e.message}"
                        } finally {
                            guardando = false
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !guardando
        ) {
            Text(
                if (guardando) "Guardando..."
                else "Guardar mascota"
            )
        }

        if (mensaje.isNotEmpty()) {
            Text(
                text = mensaje,
                color = MaterialTheme.colorScheme.primary
            )
        }

        if (error.isNotEmpty()) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        HorizontalDivider()

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Mascotas registradas",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Listado obtenido desde Django
        if (cargando) {
            CircularProgressIndicator()
        } else if (mascotas.isEmpty()) {
            Text("Todavía no hay mascotas registradas")
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(mascotas) { mascota ->
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = mascota.nombre,
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text("Especie: ${mascota.especie}")
                            Text("Raza: ${mascota.raza}")
                            Text("Edad: ${mascota.edad} años")
                        }
                    }
                }
            }
        }
    }
}
