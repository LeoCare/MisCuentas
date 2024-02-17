package com.app.miscuentas.domain.model

import android.media.Image
import java.time.LocalDateTime

data class Gasto (
    var id: Int,
    var concepto: String,
    var importe: Double,
    var imagen: IconoGasto,
    private var _fechaPago: LocalDateTime
){
    /** Establece la fecha del gasto en el momento actual **/
    var fechaPago: LocalDateTime?
        get () = _fechaPago
        set(value) {
            _fechaPago = value ?: LocalDateTime.now() //Operador 'Elvis'
        }

}

