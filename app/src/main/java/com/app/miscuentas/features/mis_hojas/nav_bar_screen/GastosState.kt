package com.app.miscuentas.features.mis_hojas.nav_bar_screen

import com.app.miscuentas.domain.model.Gasto
import com.app.miscuentas.domain.model.HojaCalculo
import com.app.miscuentas.domain.model.IconoGasto

data class GastosState (
    val hojaPrincipal: HojaCalculo? = null,
    val hojaAMostrar: HojaCalculo? = null,
    val listaGastosOk: Boolean = false,
    val idHojaPrincipal: Int? = null,
    val borrarGasto: Array<Int>? = null

)