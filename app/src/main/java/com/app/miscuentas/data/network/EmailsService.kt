package com.app.miscuentas.data.network

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.app.miscuentas.data.dto.EmailDto
import com.app.miscuentas.data.dto.HojaCrearDto
import com.app.miscuentas.data.dto.HojaDto
import com.app.miscuentas.data.pattern.repository.EmailsRepository

class EmailsService (
    private val emailRepository: EmailsRepository
) {

    /**********/
    /** API **/
    /**********/
    // Crear una nueva hoja
    suspend fun createEmailApi(emailDto: EmailDto):String? {
        return emailRepository.createEmail(emailDto)
    }
}