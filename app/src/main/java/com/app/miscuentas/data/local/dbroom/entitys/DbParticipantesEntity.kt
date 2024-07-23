package com.app.miscuentas.data.local.dbroom.entitys

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import com.app.miscuentas.domain.model.Gasto
import com.app.miscuentas.domain.model.Participante

//De esta manera personalizo el nombre de la tabla, si no, seria el de la clase
@Entity(
    tableName = "t_participantes",
    indices = [Index(value = ["nombre","idHojaParti"], unique = true), Index(value = ["idRegistroParti"])], //el nombre no puede repetirse en la BBDD
    foreignKeys = [
        ForeignKey(
            entity = DbHojaCalculoEntity::class,
            parentColumns = ["idHoja"],
            childColumns = ["idHojaParti"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DbRegistrosEntity::class,
            parentColumns = ["idRegistro"],
            childColumns = ["idRegistroParti"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class DbParticipantesEntity (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "idParticipante") val idParticipante: Long = 0,
    @ColumnInfo(name = "nombre") val nombre: String,
    @ColumnInfo(name = "correo") var correo: String? = "",
    @ColumnInfo(name = "idRegistroParti") var idRegistroParti: Long? = null,
    @ColumnInfo(name = "idHojaParti", index = true) var idHojaParti: Long? = null
)

fun DbParticipantesEntity.toDomain(gastos: List<Gasto> = listOf()) = Participante(
    idParticipante = idParticipante,
    nombre = nombre,
    correo = correo,
    listaGastos = gastos
)
