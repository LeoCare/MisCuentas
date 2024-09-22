package com.app.miscuentas.features.inicio

import com.app.miscuentas.data.local.dbroom.relaciones.HojaConParticipantes

data class InicioState(
    val huellaDigital: Boolean = false,
    val registrado: String = "",
    val idHojaPrincipal: Long = 0,
    val hojaPrincipal: HojaConParticipantes? = null,
    val totalHojas: Int = 0
)