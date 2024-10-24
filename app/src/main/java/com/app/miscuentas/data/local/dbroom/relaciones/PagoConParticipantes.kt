package com.app.miscuentas.data.local.dbroom.relaciones

import android.graphics.Bitmap
import android.net.Uri
import com.app.miscuentas.data.local.dbroom.entitys.DbPagoEntity


data class PagoConParticipantes(
    val idPago: Long,
    val idPartiDeudor: Long,
    val idPartiAcreedor: Long,
    val nombrePagador: String,
    val nombreAcreedor: String,
    val monto: Double,
    val fechaPago: String,
    var fotoPago: Bitmap?,
    val fechaConfirmacion: String? = null
)
