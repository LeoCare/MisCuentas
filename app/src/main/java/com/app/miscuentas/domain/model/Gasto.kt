package com.app.miscuentas.domain.model

import com.app.miscuentas.data.local.dbroom.dbGastos.DbGastosEntity
import java.math.BigDecimal
import java.time.LocalDateTime

data class Gasto (
    var id_gasto: Int,
    var concepto: String,
    val importe: String,
    var fecha_gasto: String?
){
    /** Establece la fecha del gasto en el momento actual **/
//    var fechaGasto: LocalDateTime?
//        get () = _fechaGasto
//        set(value) {
//            _fechaGasto = value ?: LocalDateTime.now()
//        }
//
}

fun Gasto.toEntity() = DbGastosEntity(
    id = id_gasto,
    concepto = concepto
)

