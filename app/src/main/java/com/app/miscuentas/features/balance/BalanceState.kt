package com.app.miscuentas.features.balance

import android.graphics.Bitmap
import android.net.Uri
import com.app.miscuentas.data.local.dbroom.entitys.DbPagoEntity
import com.app.miscuentas.data.local.dbroom.entitys.DbParticipantesEntity
import com.app.miscuentas.data.local.dbroom.relaciones.HojaConBalances
import com.app.miscuentas.data.local.dbroom.relaciones.HojaConParticipantes
import com.app.miscuentas.data.local.dbroom.relaciones.PagoConParticipantes

data class BalanceState (
    val idRegistrado: Long = 0,
    val idPartiRegistrado: Long = 0,
    val imagenBitmap: Bitmap? = null,
    val hojaAMostrar: HojaConParticipantes? = null,
    val hojaConBalances: HojaConBalances? = null,
    val balanceDeuda: Map<DbParticipantesEntity, Double>? = null,
    val pagoRealizado: Boolean = false,
    val pagoNewFoto: PagoConParticipantes? = null,
    val pagoAConfirmar: PagoConParticipantes? = null,
    val opcionSelected: String = "",
    val listaPagosConParticipantes: List<PagoConParticipantes>? = listOf(),
    val pendienteSubirCambios: Boolean = false
)