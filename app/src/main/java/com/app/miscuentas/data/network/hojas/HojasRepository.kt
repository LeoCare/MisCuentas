package com.app.miscuentas.data.network.hojas

import com.app.miscuentas.data.model.MisHojas

class HojasRepository ( private val hojasService: HojasService ) {

    suspend fun getPhotos(): List<MisHojas>? {
        return hojasService.getPhotos()
    }
}