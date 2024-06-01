package com.app.miscuentas.domain.model

import com.app.miscuentas.data.local.dbroom.entitys.DbHojaCalculoEntity

data class HojaCalculo(
    var idHoja: Long,
    var titulo: String,
    var fechaCreacion: String?,
    var fechaCierre: String?,
    var limite: String?,
    var status: String,
    var participantesHoja: List<Participante?>,
    var principal: Boolean = false
){
    /** Asigna la fecha de tipo LocalDate a _fechaCierre **/
//    var _fechaCierre: LocalDate?
//        get() = Validaciones.fechaToDateFormat(fechaCierre)
//        set(value){ fechaCierre = Validaciones.fechaToStringFormat(value) }

}

fun HojaCalculo.toEntity() = DbHojaCalculoEntity(
    idHoja = idHoja,
    titulo = titulo,
    fechaCreacion = fechaCreacion,
    fechaCierre = fechaCierre,
    limite = limite,
    status = status,
    principal = principal

)
