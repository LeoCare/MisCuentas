package com.app.miscuentas.data.local.dbroom.entitys

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import com.app.miscuentas.data.dto.ParticipanteDto
import com.app.miscuentas.data.model.Gasto
import com.app.miscuentas.data.model.Participante

//De esta manera personalizo el nombre de la tabla, si no, seria el de la clase
@Entity(
    tableName = "t_participantes",
    indices = [Index(value = ["nombre","idHojaParti"], unique = true), Index(value = ["idUsuarioParti"])], //el nombre no puede repetirse en la BBDD
    foreignKeys = [
        ForeignKey(
            entity = DbHojaCalculoEntity::class,
            parentColumns = ["idHoja"],
            childColumns = ["idHojaParti"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DbUsuariosEntity::class,
            parentColumns = ["idUsuario"],
            childColumns = ["idUsuarioParti"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class DbParticipantesEntity (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "idParticipante") val idParticipante: Long = 0,
    @ColumnInfo(name = "nombre") val nombre: String,
    @ColumnInfo(name = "correo") var correo: String? = null,
    @ColumnInfo(name = "idUsuarioParti") var idUsuarioParti: Long? = null,
    @ColumnInfo(name = "idHojaParti", index = true) var idHojaParti: Long,
    @ColumnInfo(name = "tipo") var tipo: String
)

fun DbParticipantesEntity.toDomain(gastos: List<Gasto> = listOf()) = Participante(
    idParticipante = idParticipante,
    nombre = nombre,
    correo = correo,
    listaGastos = gastos
)

fun DbParticipantesEntity.toDto() = ParticipanteDto(
    idParticipante = idParticipante,
    nombre = nombre,
    correo = correo,
    tipo = tipo,
    idUsuario = idUsuarioParti,
    idHoja = idHojaParti
)
