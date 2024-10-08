package com.app.miscuentas.data.local.dbroom.entitys

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.app.miscuentas.data.model.HojaCalculo
import com.app.miscuentas.data.model.Participante

@Entity(
    tableName = "t_hojas_cab",
    foreignKeys = [
        ForeignKey(
            entity = DbUsuariosEntity::class,
            parentColumns = ["idUsuario"],
            childColumns = ["idUsuarioHoja"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DbHojaCalculoEntity (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "idHoja") var idHoja: Long = 0,
    @ColumnInfo(name = "titulo") var titulo: String = "",
    @ColumnInfo(name = "fechaCreacion") var fechaCreacion: String,
    @ColumnInfo(name = "fechaCierre") var fechaCierre: String?,
    @ColumnInfo(name = "limite") var limite: String?,
    @ColumnInfo(name = "status") var status: String,
    @ColumnInfo(name = "idUsuarioHoja", index = true) var idUsuarioHoja: Long = 0,
    @ColumnInfo(name = "propietaria") var propietaria: String = "S"
)

fun DbHojaCalculoEntity.toDomain(participantes: List<Participante> = listOf()) = HojaCalculo(
    idHoja = idHoja,
    titulo = titulo,
    fechaCreacion = fechaCreacion,
    fechaCierre = fechaCierre,
    limite = limite,
    status = status,
    participantesHoja = participantes,
    idUsuarioHoja = idUsuarioHoja,
    propietaria = propietaria
)

