package com.app.miscuentas.data.model

import com.app.miscuentas.data.local.dbroom.entitys.DbParticipantesEntity
import com.app.miscuentas.domain.dto.ParticipanteDto

data class Participante(
    var idParticipante: Long = 0,
    var nombre: String,
    var correo: String? = "",
    var listaGastos: List<Gasto?> = listOf()
)

fun Participante.toEntity() = DbParticipantesEntity(
    idParticipante = idParticipante,
    nombre = nombre,
    correo = correo
)

fun Participante.toEntityWithHoja(idHojaCalculo: Long) = DbParticipantesEntity(
    idParticipante = idParticipante,
    nombre = nombre,
    correo = correo,
    idHojaParti = idHojaCalculo
)

fun Participante.toEntityWithUsuario(idUsuarioParti: Long) = DbParticipantesEntity(
    idParticipante = idParticipante,
    nombre = nombre,
    correo = correo,
    idUsuarioParti = idUsuarioParti
)

fun ParticipanteDto.toEntity() = DbParticipantesEntity(
    idParticipante = idParticipante,
    nombre = nombre,
    correo = correo,
    idUsuarioParti = idUsuario,
    idHojaParti = idHoja
)

fun List<ParticipanteDto>.toEntityList(): List<DbParticipantesEntity> {
    return this.map { participanteDto ->
        participanteDto.toEntity()
    }
}