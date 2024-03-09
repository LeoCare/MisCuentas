package com.app.miscuentas.domain.model

import android.media.Image
import com.app.miscuentas.data.local.dbroom.dbGastos.DbGastosEntity
import java.time.LocalDateTime

data class Gasto (
    var id: Int,
    var concepto: String,
    var imagen: IconoGasto?,
    private var _fechaPago: LocalDateTime?
){
    /** Establece la fecha del gasto en el momento actual **/
    var fechaPago: LocalDateTime?
        get () = _fechaPago
        set(value) {
            _fechaPago = value ?: LocalDateTime.now()
        }

}

fun Gasto.toEntity() = DbGastosEntity(
    id = id,
    concepto = concepto
)

