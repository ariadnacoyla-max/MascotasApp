
package com.tecsup.mascotasapp.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Configura la conexión entre Android y Django
object RetrofitClient {

    // Dirección del servidor Django desde el emulador
    private const val BASE_URL = "http://10.0.2.2:8000/"

    // Crea el servicio para utilizar las funciones CRUD
    val servicio: MascotaApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MascotaApiService::class.java)
    }
}
