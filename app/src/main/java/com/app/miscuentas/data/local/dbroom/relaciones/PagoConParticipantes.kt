package com.app.miscuentas.data.local.dbroom.relaciones

import android.net.Uri


data class PagoConParticipantes(
    val nombrePagador: String,
    val nombreAcreedor: String,
    val monto: Double,
    val fechaPago: String,
    val fotoPago: Uri?,
    val confirmado: Boolean = false
)