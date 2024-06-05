package com.app.miscuentas.features.nav_bar_screens.mis_gastos

import com.app.miscuentas.data.local.dbroom.relaciones.HojaConParticipantes
import com.app.miscuentas.domain.model.HojaCalculo

data class MisGastosState (
    val idHojaPrincipal: Long? = null,
    val hojaAMostrar: HojaConParticipantes? = null
)