package com.app.miscuentas.domain

import com.app.miscuentas.data.model.MisHojas
import com.app.miscuentas.data.network.hojas.HojasRepository

class GetMisHojas( private val hojasRepository: HojasRepository) {
    suspend fun getPhotos(): List<MisHojas>?{
        return hojasRepository.getPhotos()
    }

}