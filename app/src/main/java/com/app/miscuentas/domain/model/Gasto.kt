package com.app.miscuentas.domain.model

import java.math.BigDecimal

data class Gasto (
    var id_gasto: String,
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

}

//fun Gasto.toEntity() = DbGastosEntity(
//    id = id,
//    concepto = concepto
//)

