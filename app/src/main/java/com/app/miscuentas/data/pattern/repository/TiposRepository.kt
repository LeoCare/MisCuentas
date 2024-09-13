package com.app.miscuentas.data.pattern.repository

import com.app.miscuentas.data.dto.TipoBalanceDto
import com.app.miscuentas.data.dto.TipoPerfilDto
import com.app.miscuentas.data.dto.TipoStatusDto
import com.app.miscuentas.data.network.TipoBalanceService
import com.app.miscuentas.data.network.TipoPerfilService
import com.app.miscuentas.data.network.TipoStatusService

class TipoPerfilRepository(
    private val tipoPerfilService: TipoPerfilService
) {

    // Obtener todos los tipos de perfil
    suspend fun getAllTipoPerfil(token: String): List<TipoPerfilDto>? {
        return tipoPerfilService.getAllTipoPerfil(token)
    }
}

class TipoBalanceRepository(
    private val tipoBalanceService: TipoBalanceService
) {

    // Obtener todos los tipos de balance
    suspend fun getAllTipoBalance(token: String): List<TipoBalanceDto>? {
        return tipoBalanceService.getAllTipoBalance(token)
    }
}

class TipoStatusRepository(private val tipoStatusService: TipoStatusService) {

    // Obtener todos los tipos de status
    suspend fun getAllTipoStatus(token: String): List<TipoStatusDto>? {
        return tipoStatusService.getAllTipoStatus(token)
    }
}


