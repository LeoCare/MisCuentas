package com.app.miscuentas.data.network.webservices

import com.app.miscuentas.data.model.Hoja
import retrofit2.http.GET

interface WebService {
    @GET("realestate")
    suspend fun getPhotos(): List<Hoja>
}