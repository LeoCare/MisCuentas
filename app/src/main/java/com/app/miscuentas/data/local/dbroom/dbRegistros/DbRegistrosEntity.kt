package com.app.miscuentas.data.local.dbroom.dbRegistros

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.app.miscuentas.domain.model.Registro

@Entity(
    tableName = "t_registros",
    indices = [Index(value = ["correo"], unique = true)]
)
class DbRegistrosEntity (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Int = 0,
    @ColumnInfo(name = "nombre") val nombre: String = "",
    @ColumnInfo(name = "correo") val correo: String = "",
    @ColumnInfo(name = "contrasenna") val contrasenna: String = ""
)

fun DbRegistrosEntity.toDomain() = Registro(
    id = id,
    nombre = nombre,
    correo = correo,
    contrasenna = contrasenna
)
