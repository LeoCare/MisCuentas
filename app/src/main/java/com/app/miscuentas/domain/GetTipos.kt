package com.app.miscuentas.domain

import com.app.miscuentas.data.dto.TipoBalanceDto
import com.app.miscuentas.data.dto.TipoPerfilDto
import com.app.miscuentas.data.dto.TipoStatusDto
import com.app.miscuentas.data.network.tipos.TipoBalanceRepository
import com.app.miscuentas.data.network.tipos.TipoPerfilRepository
import com.app.miscuentas.data.network.tipos.TipoStatusRepository

class GetTipos(
    private val tipoPerfilRepository: TipoPerfilRepository
) {

    // Obtener todos los tipos de perfil
    suspend fun getAllTipoPerfil(token: String): List<TipoPerfilDto>? {
        return tipoPerfilRepository.getAllTipoPerfil(token)
    }
}

class TipoBalanceRepository(
    private val tipoBalanceRepository: TipoBalanceRepository
) {

    // Obtener todos los tipos de balance
    suspend fun getAllTipoBalance(token: String): List<TipoBalanceDto>? {
        return tipoBalanceRepository.getAllTipoBalance(token)
    }
}

class TipoStatusRepository(
    private val tipoStatusRepository: TipoStatusRepository
) {

    // Obtener todos los tipos de status
    suspend fun getAllTipoStatus(token: String): List<TipoStatusDto>? {
        return tipoStatusRepository.getAllTipoStatus(token)
    }
}