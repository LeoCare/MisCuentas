package com.app.miscuentas.data.model

import com.app.miscuentas.data.local.dbroom.entitys.DbHojaCalculoEntity
import com.app.miscuentas.data.dto.HojaDto

data class HojaCalculo(
    var idHoja: Long,
    var titulo: String,
    var fechaCreacion: String,
    var fechaCierre: String?,
    var limite: String?,
    var status: String,
    var participantesHoja: List<Participante?>,
    var idUsuarioHoja: Long = 0,
    var propietaria: String = "S"
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
    idUsuarioHoja = idUsuarioHoja,
    propietaria = propietaria
)

fun HojaDto.toEntity() = DbHojaCalculoEntity(
    idHoja = idHoja,
    titulo = titulo,
    fechaCreacion = fechaCreacion,
    fechaCierre = fechaCierre,
    limite = limiteGastos?.toString(),
    status = status,
    idUsuarioHoja = idUsuario

)

fun HojaCalculo.toDto() = HojaDto(
    idHoja = idHoja,
    titulo = titulo,
    fechaCreacion = fechaCreacion,
    fechaCierre = fechaCierre,
    limiteGastos = limite?.replace(',','.')?.toDouble(),
    status = status,
    idUsuario = idUsuarioHoja
)