package com.app.miscuentas.domain.model

import com.app.miscuentas.data.local.dbroom.dbHojaCalculo.DbHojaCalculoEntity
import com.app.miscuentas.domain.Validaciones
import java.time.LocalDate

data class HojaCalculo(
    var id: Int,
    var titulo: String,
    var fechaCierre: String?,
    var limite: Double?,
    var status: Char,
    var participantesHoja: List<Participante>? = null
){
    /** Asigna la fecha de tipo LocalDate a _fechaCierre **/
    var _fechaCierre: LocalDate?
        get() = Validaciones.fechaToDateFormat(fechaCierre)
        set(value){ fechaCierre = Validaciones.fechaToStringFormat(value) }

}

