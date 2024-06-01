package com.app.miscuentas.domain.model

import com.app.miscuentas.data.local.dbroom.entitys.DbParticipantesEntity

data class Participante(
    var idParticipante: Long = 0,
    var nombre: String,
    var correo: String? = "",
    var listaGastos: List<Gasto?> = listOf()
)

fun Participante.toEntity(idHojaCalculo: Long) = DbParticipantesEntity(
    idParticipante = idParticipante,
    nombre = nombre,
    correo = correo,
    idHojaParti = idHojaCalculo
)