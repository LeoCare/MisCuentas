package com.app.miscuentas.domain.model

import com.app.miscuentas.data.local.dbroom.entitys.DbRegistrosEntity

data class Registro(
    var id: Int = 0,
    var nombre: String = "",
    var correo: String = "",
    var contrasenna: String = ""
)

fun Registro.toEntity() = DbRegistrosEntity(
    id = id,
    nombre = nombre,
    correo = correo,
    contrasenna = contrasenna
)
