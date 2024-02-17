package com.app.miscuentas.data.network.hojas

import com.app.miscuentas.data.model.Hoja

class HojasRepository ( private val hojasService: HojasService ) {

    suspend fun getPhotos(): List<Hoja>? {
        return hojasService.getPhotos()
    }
}