package com.app.miscuentas.features.balances

import android.net.Uri
import com.app.miscuentas.data.local.dbroom.relaciones.HojaConParticipantes

data class BalanceState (
    val imagenPago: String? = null,
    val imagenUri: Uri? = null,
    val imagenAbsolutePath: String? = null,
    val hojaDeGastos: HojaConParticipantes? = null,
    val balanceDeuda: Map<String, Double>? = null, //importe que debe/recibe cada participante hasta antes del cierre
    val existeRegistrado: Boolean = false,
)