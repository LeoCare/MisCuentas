package com.app.miscuentas.model

import com.app.miscuentas.db.dbParticipantes.DbParticipantesEntity

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