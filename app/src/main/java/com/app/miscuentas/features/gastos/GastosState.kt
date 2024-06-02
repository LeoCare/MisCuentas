package com.app.miscuentas.features.gastos

import com.app.miscuentas.data.local.dbroom.entitys.DbGastosEntity
import com.app.miscuentas.data.local.dbroom.relaciones.HojaConParticipantes
import com.app.miscuentas.domain.model.HojaCalculo

data class GastosState (
    val hojaPrincipal: HojaCalculo? = null,
    val hojaAMostrar: HojaConParticipantes? = null,
    val listaGastosOk: Boolean = false,
    val idHojaPrincipal: Long? = null,
    val gastoElegido: DbGastosEntity? = null

)