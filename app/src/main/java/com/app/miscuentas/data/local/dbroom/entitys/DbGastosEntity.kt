package com.app.miscuentas.data.local.dbroom.entitys

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import com.app.miscuentas.data.model.Gasto


@Entity(
    tableName = "t_gastos",
    indices = [Index(value =  ["idGasto"])],
    foreignKeys = [
        ForeignKey(
            entity = DbParticipantesEntity::class,
            parentColumns = ["idParticipante"],
            childColumns = ["idParticipanteGasto"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DbGastosEntity (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "idGasto") var idGasto: Long = 0,
    @ColumnInfo(name = "tipo") var tipo: Long = 0,
    @ColumnInfo(name = "concepto") var concepto: String = "",
    @ColumnInfo(name = "importe") var importe: String = "0.0",
    @ColumnInfo(name = "fecha_gasto") var fechaGasto: String?,
    @ColumnInfo(name = "idParticipanteGasto", index = true) var idParticipanteGasto: Long?,
    @ColumnInfo(name = "idFotoGasto", index = true) var idFotoGasto: Long? = null

)

fun DbGastosEntity.toDomain(foto: Uri?) = Gasto(
    idGasto = idGasto,
    tipo = tipo,
    concepto = concepto,
    importe = importe,
    fechaGasto = fechaGasto,
    fotoGastoUri = foto
)


