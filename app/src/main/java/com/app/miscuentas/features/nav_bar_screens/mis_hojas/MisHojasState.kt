package com.app.miscuentas.features.nav_bar_screens.mis_hojas

import com.app.miscuentas.data.local.dbroom.relaciones.HojaConParticipantes
import com.app.miscuentas.data.model.HojaCalculo

data class MisHojasState(
    val listaHojasConParticipantes: List<HojaConParticipantes> =  emptyList(),
    val listaHojasAMostrar: List<HojaConParticipantes> =  emptyList(),
    val hojaAModificar: HojaCalculo? = null,
    val opcionSelected: String = "",
    val nuevoStatusHoja: String = "",
    val idRegistro: Long = 0,
    val descending: Boolean = false,
    val ordenElegido: String = "Fecha Creacion",
    val filtroElegido: String  = "Todos",
    val filtroTipoElegido: String = "",
    val filtroEstadoElegido: String = "",
    val eleccionEnTitulo: String = "",
    val pendienteSubirCambios: Boolean = false,
    val isRefreshing: Boolean = false
)
