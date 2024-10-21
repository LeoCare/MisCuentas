package com.app.miscuentas.features.nav_bar_screens.participantes

import com.app.miscuentas.data.local.dbroom.relaciones.HojaConParticipantes
import com.app.miscuentas.data.local.dbroom.relaciones.ParticipanteConGastos
import com.app.miscuentas.data.model.Participante

data class ParticipantesState(
    val idUsuario: Long? = null,
    val idHojaPrincipal: Long? = null,
    val hojasDelRegistrado: List<HojaConParticipantes> = listOf(),
    val listaParticipantes: List<ParticipanteConGastos> = listOf(),
    val listaParticipantesAMostrar: List<ParticipanteConGastos> = listOf(),
    val filtroElegido: String = "Todos",
    val filtroHojaElegido: Long = 0,
    val filtroTipoElegido: String = "",
    val eleccionEnTitulo: String = "",
    val ordenElegido: String = "Nombre",
    val descending: Boolean = false
)
