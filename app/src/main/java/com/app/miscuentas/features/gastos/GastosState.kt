package com.app.miscuentas.features.gastos

import com.app.miscuentas.data.local.dbroom.entitys.DbGastosEntity
import com.app.miscuentas.data.local.dbroom.relaciones.HojaConBalances
import com.app.miscuentas.data.local.dbroom.relaciones.HojaConParticipantes
import com.app.miscuentas.domain.model.HojaCalculo

data class GastosState (
    val hojaPrincipal: HojaCalculo? = null,
    val hojaConBalances: HojaConBalances? = null,
    val idRegistrado: Long = 0,
    val existeRegistrado: Boolean = false,
    val hojaAMostrar: HojaConParticipantes? = null,
    val listaGastosOk: Boolean = false,
    val idHojaPrincipal: Long? = null,
    val gastoElegido: DbGastosEntity? = null,
    val cierreAceptado: Boolean = false,
    val resumenGastos: Map<String, Double>? = null, //suma de gastos por participante
    val balanceDeuda: Map<String, Double>? = null //importe que debe/recibe cada participante hasta antes del cierre

)