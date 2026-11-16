
package com.tecsup.mascotasapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.tecsup.mascotasapp.ui.theme.MascotasAppTheme
import com.tecsup.mascotasapp.view.ScreenMascotas

// Actividad principal de la aplicación
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Muestra la pantalla de mascotas
        setContent {
            MascotasAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ScreenMascotas()
                }
            }
        }
    }
}
