package com.app.miscuentas.data.model

import com.app.miscuentas.data.local.dbroom.entitys.DbUsuariosEntity
import com.app.miscuentas.domain.dto.UsuarioDto
import com.app.miscuentas.domain.dto.UsuarioLoginDto

data class Usuario(
    val contrasenna: String,
    val correo: String,
    val idUsuario: Long = 0,
    val nombre: String,
    val perfil: String?
)

fun Usuario.toEntity() = DbUsuariosEntity(
    idUsuario = idUsuario,
    nombre = nombre,
    correo = correo,
    contrasenna = contrasenna
)

// Convertir de entidad a DTO
fun DbUsuariosEntity.toDto() = UsuarioDto(
    idUsuario = this.idUsuario,
    nombre = this.nombre,
    correo = this.correo,
    contrasenna = this.contrasenna,
    perfil = perfil ?: "USER"
)

// Convertir de DTO a entidad
fun UsuarioDto.toEntity() = DbUsuariosEntity(
    idUsuario = this.idUsuario,
    nombre = this.nombre,
    correo = this.correo,
    contrasenna = this.contrasenna,
    perfil = perfil
)

// Convertir de DTO a entidad
fun DbUsuariosEntity.toLogin() = UsuarioLoginDto(
    correo = this.correo,
    contrasenna = this.contrasenna
)
