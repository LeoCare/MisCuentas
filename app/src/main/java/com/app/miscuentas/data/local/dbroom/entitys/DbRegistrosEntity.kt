package com.app.miscuentas.data.local.dbroom.entitys

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.app.miscuentas.data.model.Registro

@Entity(
    tableName = "t_registros",
    indices = [Index(value = ["correo"], unique = true)]
)
data class DbRegistrosEntity (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "idRegistro") val idRegistro: Long = 0,
    @ColumnInfo(name = "nombre") val nombre: String = "",
    @ColumnInfo(name = "correo") val correo: String = "",
    @ColumnInfo(name = "contrasenna") val contrasenna: String = ""
)

fun DbRegistrosEntity.toDomain() = Registro(
    idRegistro = idRegistro,
    nombre = nombre,
    correo = correo,
    contrasenna = contrasenna
)
