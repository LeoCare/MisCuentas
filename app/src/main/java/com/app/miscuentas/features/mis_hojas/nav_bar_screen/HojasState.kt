package com.app.miscuentas.features.mis_hojas.nav_bar_screen

import com.app.miscuentas.data.model.Hoja
import com.app.miscuentas.domain.model.HojaCalculo

data class HojasState(
    /** API **/ //val listaHojas: List<Hoja> = listOf(), hoja de data/model/Hoja
    val listaHojas: List<HojaCalculo>? = null,
    val listaHojasAMostrar: List<HojaCalculo>? = null,
    val circularIndicator: Boolean = true,
    val hojaPrincipal: HojaCalculo? = null,
    val tipoOrden: String = "Fecha creacion",
    val ordenDesc: Boolean = true,
    val mostrarTipo: String = "T"
)
