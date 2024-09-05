package com.app.miscuentas.data.model

import com.app.miscuentas.data.local.dbroom.entitys.DbRegistrosEntity

data class Registro(
    var idRegistro: Long = 0,
    var nombre: String = "",
    var correo: String = "",
    var contrasenna: String = ""
)

fun Registro.toEntity() = DbRegistrosEntity(
    idRegistro = idRegistro,
    nombre = nombre,
    correo = correo,
    contrasenna = contrasenna
)
