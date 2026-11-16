
package com.tecsup.mascotasapp.data

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.Proxy
import java.util.concurrent.TimeUnit

// Configuración de la conexión con Django
object RetrofitClient {

    // Dirección local mediante ADB Reverse
    private const val BASE_URL = "http://127.0.0.1:8000/"

    // Cliente HTTP para realizar las solicitudes
    private val cliente = OkHttpClient.Builder()
        .proxy(Proxy.NO_PROXY)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    // Servicio para consultar y modificar mascotas
    val servicio: MascotaApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(cliente)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MascotaApiService::class.java)
    }
}
