package com.app.miscuentas.features.balance

import android.graphics.Bitmap
import android.net.Uri
import com.app.miscuentas.data.local.dbroom.entitys.DbPagoEntity
import com.app.miscuentas.data.local.dbroom.relaciones.HojaConBalances
import com.app.miscuentas.data.local.dbroom.relaciones.HojaConParticipantes
import com.app.miscuentas.data.local.dbroom.relaciones.PagoConParticipantes

data class BalanceState (
    val imagenBitmap: Bitmap? = null,
    val hojaAMostrar: HojaConParticipantes? = null,
    val hojaConBalances: HojaConBalances? = null,
    val balanceDeuda: Map<String, Double>? = null, //importe que debe/recibe cada participante hasta antes del cierre
    val pagoRealizado: Boolean = false,
    val pagoNewFoto: PagoConParticipantes? = null,
    val listaPagosConParticipantes: List<PagoConParticipantes>? = listOf()
)