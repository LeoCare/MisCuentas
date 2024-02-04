package com.app.miscuentas.data.network.hojas

import android.content.ContentValues
import android.util.Log
import com.app.miscuentas.data.model.MisHojas
import com.app.miscuentas.data.network.webservices.WebService

class HojasService (private val webService: WebService) {

    suspend fun getPhotos(): List<MisHojas>? {
        return try {
            val response = webService.getPhotos()

            if (response.isNotEmpty()) {
                response
            } else {
                null
            }
        } catch (e: Exception) {
            Log.d(ContentValues.TAG, "Solicitud fallida: $e")
            null
        }
    }
}