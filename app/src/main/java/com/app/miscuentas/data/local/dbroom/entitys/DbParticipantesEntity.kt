package com.app.miscuentas.data.local.dbroom.entitys

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.app.miscuentas.domain.model.Gasto
import com.app.miscuentas.domain.model.Participante
//De esta manera personalizo el nombre de la tabla, si no, seria el de la clase
@Entity(
    tableName = "t_participantes",
    indices = [Index(value = ["nombre","idHojaParti"], unique = true)] //el nombre no puede repetirse en la BBDD
)
data class DbParticipantesEntity (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "idParticipante") val idParticipante: Long = 0,
    @ColumnInfo(name = "nombre") val nombre: String,
    @ColumnInfo(name = "correo") var correo: String? = "",
    @ColumnInfo(name = "idHojaParti") var idHojaParti: Long = 0
)

fun DbParticipantesEntity.toDomain(gastos: List<Gasto> = listOf()) = Participante(
    idParticipante = idParticipante,
    nombre = nombre,
    correo = correo,
    listaGastos = gastos
)
