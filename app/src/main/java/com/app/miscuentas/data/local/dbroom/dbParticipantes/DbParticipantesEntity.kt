package com.app.miscuentas.data.local.dbroom.dbParticipantes

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.app.miscuentas.domain.model.Participante

@Entity(tableName = "t_participantes") //De esta manera personalizo el nombre de la tabla, si no, seria el de la clase
class DbParticipantesEntity (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Int? = 0,
    @ColumnInfo(name = "nombre") val nombre: String,
    @ColumnInfo(name = "correo") var correo: String? = ""
)

fun DbParticipantesEntity.toDomain() = Participante(
    id = id,
    nombre = nombre,
    correo = correo
)