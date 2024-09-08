package com.app.miscuentas.domain

import com.app.miscuentas.data.dto.TipoBalanceDto
import com.app.miscuentas.data.dto.TipoPerfilDto
import com.app.miscuentas.data.dto.TipoStatusDto
import com.app.miscuentas.data.pattern.TipoBalanceRepository
import com.app.miscuentas.data.pattern.TipoPerfilRepository
import com.app.miscuentas.data.pattern.TipoStatusRepository

class GetTipos(
    private val tipoPerfilRepository: TipoPerfilRepository,
    private val tipoBalanceRepository: TipoBalanceRepository,
    private val tipoStatusRepository: TipoStatusRepository
) {

    // Obtener todos los tipos de perfil
    suspend fun getAllTipoPerfil(token: String): List<TipoPerfilDto>? {
        return tipoPerfilRepository.getAllTipoPerfil(token)
    }


    // Obtener todos los tipos de balance
    suspend fun getAllTipoBalance(token: String): List<TipoBalanceDto>? {
        return tipoBalanceRepository.getAllTipoBalance(token)
    }


    // Obtener todos los tipos de status
    suspend fun getAllTipoStatus(token: String): List<TipoStatusDto>? {
        return tipoStatusRepository.getAllTipoStatus(token)
    }
}