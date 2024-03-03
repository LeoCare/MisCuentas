package com.app.miscuentas.domain.model

import com.app.miscuentas.data.local.dbroom.dbHojaCalculo.DbHojaCalculoEntity
import com.app.miscuentas.data.local.dbroom.dbParticipantes.toDomain

data class HojaCalculo(
    var id: Int,
    var titulo: String,
    var fechaCierre: String?,
    var limite: Double?,
    var status: String,
    var participantesHoja: List<Participante>?,
    var principal: Boolean = false
){
    /** Asigna la fecha de tipo LocalDate a _fechaCierre **/
//    var _fechaCierre: LocalDate?
//        get() = Validaciones.fechaToDateFormat(fechaCierre)
//        set(value){ fechaCierre = Validaciones.fechaToStringFormat(value) }

}

fun HojaCalculo.toEntity() = DbHojaCalculoEntity(
    id = id,
    titulo = titulo,
    fechaCierre = fechaCierre,
    limite = limite,
    status = status,
    principal = if(principal) "S" else "N"

)
