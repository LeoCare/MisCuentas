package com.app.miscuentas.features.nav_bar_screens.mis_gastos

import com.app.miscuentas.data.local.dbroom.entitys.DbGastosEntity
import com.app.miscuentas.data.local.dbroom.relaciones.HojaConParticipantes
import com.app.miscuentas.data.local.dbroom.relaciones.ParticipanteConGastos
import com.app.miscuentas.domain.model.Gasto
import com.app.miscuentas.domain.model.HojaCalculo

data class MisGastosState (
    val idRegistro: Long? = null,
    val idHojaPrincipal: Long? = null,
    val hojasDelRegistrado: List<HojaConParticipantes> = listOf(),
    val hojaAMostrar: HojaConParticipantes? = null,
    val listaGastos: List<DbGastosEntity> = listOf(),
    val listaGastosAMostrar: List<DbGastosEntity>? = listOf(),
    val filtroElegido: String = "Todos",
    val filtroHojaElegido: Long = 0,
    val filtroTipoElegido: Long = 0,
    val ordenElegido: String = "Fecha",
    val descending: Boolean = false

)