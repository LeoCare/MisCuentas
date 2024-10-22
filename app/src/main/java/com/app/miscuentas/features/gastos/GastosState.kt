package com.app.miscuentas.features.gastos

import android.graphics.Bitmap
import android.net.Uri
import com.app.miscuentas.data.local.dbroom.entitys.DbGastosEntity
import com.app.miscuentas.data.local.dbroom.entitys.DbPagoEntity
import com.app.miscuentas.data.local.dbroom.entitys.DbParticipantesEntity
import com.app.miscuentas.data.local.dbroom.relaciones.HojaConBalances
import com.app.miscuentas.data.local.dbroom.relaciones.HojaConParticipantes
import com.app.miscuentas.data.local.dbroom.relaciones.PagoConParticipantes
import com.app.miscuentas.data.model.HojaCalculo

data class GastosState (
    val hojaConBalances: HojaConBalances? = null,
    val hojaAMostrar: HojaConParticipantes? = null,
    val totalGastosActual: Double = 0.0,
    val gastoABorrar: DbGastosEntity? = null,
    val gastoNewFoto: DbGastosEntity? = null,
    val cierreAceptado: Boolean = false,
    val sumaParticipantes: Map<String, Double>? = null, //suma de gastos por participante
    val balanceDeuda: Map<DbParticipantesEntity, Double>? = null, //importe que debe/recibe cada participante hasta antes del cierre
    val imagenBitmap: Bitmap? = null,
    val mostrarFoto: Boolean = false,
    val pendienteSubirCambios: Boolean = false
)