package com.app.miscuentas.data.local.dbroom.dbParticipantes

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.app.miscuentas.data.local.dbroom.dbHojaCalculo.DbHojaCalculoEntity
import com.app.miscuentas.domain.model.Participante
//De esta manera personalizo el nombre de la tabla, si no, seria el de la clase
@Entity(
    tableName = "t_participantes",
    primaryKeys = ["id"],
    indices = [Index(value = ["nombre"], unique = true)] //el nombre no puede repetirse en la BBDD
)
class DbParticipantesEntity (
    @ColumnInfo(name = "id") val id: Int = 0,
    @ColumnInfo(name = "nombre") val nombre: String,
    @ColumnInfo(name = "correo") var correo: String? = ""
)

fun DbParticipantesEntity.toDomain() = Participante(
    id = id,
    nombre = nombre,
    correo = correo,
    listaGastos = null
)