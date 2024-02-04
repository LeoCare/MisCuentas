package com.app.miscuentas.domain.model

import com.app.miscuentas.data.local.dbroom.dbParticipantes.DbParticipantesEntity

data class Participante(
   // var id: Int? = 0,
    var nombre: String,
    var correo: String? = "", //se inicializa para simular una sobrecarga como en Java
    var listaGastos: List<Gasto>? = null
)

fun Participante.toEntity() = DbParticipantesEntity(
    //id = id,
    nombre = nombre,
    correo = correo
)