package com.app.miscuentas.data.local.dbroom.entitys

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.app.miscuentas.data.model.Usuario

@Entity(
    tableName = "t_usuarios",
    indices = [Index(value = ["correo"], unique = true)]
)
data class DbUsuariosEntity (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "idUsuario") val idUsuario: Long = 0,
    @ColumnInfo(name = "nombre") val nombre: String = "",
    @ColumnInfo(name = "correo") val correo: String = "",
    @ColumnInfo(name = "contrasenna") val contrasenna: String = "",
    @ColumnInfo(name = "perfil") val perfil: String? = "USER"
)

fun DbUsuariosEntity.toDomain() = Usuario(
    idUsuario = idUsuario,
    nombre = nombre,
    correo = correo,
    contrasenna = contrasenna,
    perfil = perfil
)
