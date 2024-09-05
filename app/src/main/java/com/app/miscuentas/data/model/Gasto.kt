package com.app.miscuentas.data.model

import android.net.Uri
import com.app.miscuentas.data.local.dbroom.entitys.DbGastosEntity

data class Gasto (
    var idGasto: Long,
    var tipo: Long = 0,
    var concepto: String,
    val importe: String,
    var fechaGasto: String?,
    var fotoGastoUri: Uri?
){
    /** Establece la fecha del gasto en el momento actual **/
//    var fechaGasto: LocalDateTime?
//        get () = _fechaGasto
//        set(value) {
//            _fechaGasto = value ?: LocalDateTime.now()
//        }
//
}

fun Gasto.toEntity(idParticipante: Long) = DbGastosEntity(
    tipo = tipo,
    concepto = concepto,
    importe = importe,
    fechaGasto = fechaGasto,
    idParticipanteGasto = idParticipante
)

