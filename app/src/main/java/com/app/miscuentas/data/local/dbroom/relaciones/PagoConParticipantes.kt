package com.app.miscuentas.data.local.dbroom.relaciones


data class PagoConParticipantes(
    val nombrePagador: String,
    val nombreAcreedor: String,
    val monto: Double,
    val fechaPago: String,
    val confirmado: Boolean = false
)