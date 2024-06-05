package com.app.miscuentas.data.local.dbroom.entitys

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.app.miscuentas.domain.model.HojaCalculo
import com.app.miscuentas.domain.model.Participante

@Entity( tableName = "t_hojas_cab" )
data class DbHojaCalculoEntity (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "idHoja") var idHoja: Long = 0,
    @ColumnInfo(name = "titulo") var titulo: String = "",
    @ColumnInfo(name = "fechaCreacion") var fechaCreacion: String?,
    @ColumnInfo(name = "fechaCierre") var fechaCierre: String?,
    @ColumnInfo(name = "limite") var limite: String?,
    @ColumnInfo(name = "status") var status: String,
    @ColumnInfo(name = "idRegistroHoja") var idRegistroHoja: Long = 0
)

fun DbHojaCalculoEntity.toDomain(participantes: List<Participante> = listOf()) = HojaCalculo(
    idHoja = idHoja,
    titulo = titulo,
    fechaCreacion = fechaCreacion,
    fechaCierre = fechaCierre,
    limite = limite,
    status = status,
    participantesHoja = participantes,
    idRegistroHoja = idRegistroHoja
)

