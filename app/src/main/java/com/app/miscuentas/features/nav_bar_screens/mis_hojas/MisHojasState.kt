package com.app.miscuentas.features.nav_bar_screens.mis_hojas

import com.app.miscuentas.data.local.dbroom.relaciones.HojaConParticipantes
import com.app.miscuentas.domain.model.HojaCalculo

data class MisHojasState(
    /** API **/ //val listaHojas: List<Hoja> = listOf(), hoja de data/model/Hoja
    val listaHojas: List<HojaCalculo>? = null,
    val listaHojasConParticipantes: List<HojaConParticipantes>? = null,
    val listaHojasAMostrar: List<HojaConParticipantes>? = null,
    val circularIndicator: Boolean = true,
    val tipoOrden: String = "Fecha creacion",
    val ordenDesc: Boolean = true,
    val mostrarTipo: String = "T",
    val hojaAModificar: HojaCalculo? = null,
    val opcionSelected: String = "",
    val nuevoStatusHoja: String = "",
    val idRegistro: Long = 0,
)
