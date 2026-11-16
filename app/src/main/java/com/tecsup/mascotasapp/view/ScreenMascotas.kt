
package com.tecsup.mascotasapp.view

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tecsup.mascotasapp.data.MascotaModel
import com.tecsup.mascotasapp.data.RetrofitClient

// Pantalla que muestra las mascotas registradas en Django
@Composable
fun ScreenMascotas() {

    var mascotas by remember {
        mutableStateOf<List<MascotaModel>>(emptyList())
    }

    var cargando by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf("") }

    // Consulta las mascotas mediante la API REST
    LaunchedEffect(Unit) {
        try {
            mascotas = RetrofitClient.servicio.listarMascotas()
        } catch (e: Exception) {
            Log.e("MASCOTAS_API", "Error al cargar mascotas", e)
            error = "No se pudieron cargar las mascotas: ${e.message}"
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

        if (cargando) {
            CircularProgressIndicator()
        } else if (error.isNotEmpty()) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error
            )
        } else if (mascotas.isEmpty()) {
            Text("Todavía no hay mascotas registradas")
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
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
