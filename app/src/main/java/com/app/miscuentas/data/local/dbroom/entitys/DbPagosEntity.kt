package com.app.miscuentas.data.local.dbroom.entitys

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.app.miscuentas.data.model.Pago

@Entity(
    tableName = "t_pagos",
    indices = [Index(value =  ["idPago"])],
    foreignKeys = [
        ForeignKey(
            entity = DbBalancesEntity::class,
            parentColumns = ["idBalance"],
            childColumns = ["idBalance"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DbBalancesEntity::class,
            parentColumns = ["idBalance"],
            childColumns = ["idBalancePagado"],
            onDelete = ForeignKey.CASCADE
        )
        ,
        ForeignKey(
            entity = DbParticipantesEntity::class,
            parentColumns = ["idParticipante"],
            childColumns = ["idParticipantePago"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DbPagoEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "idPago") var idPago: Long = 0,
    @ColumnInfo(name = "idParticipantePago", index = true) val idParticipantePago: Long,
    @ColumnInfo(name = "idBalance", index = true) val idBalance: Long,
    @ColumnInfo(name = "idBalancePagado", index = true) val idBalancePagado: Long,
    @ColumnInfo(name = "monto") val monto: Double,
    @ColumnInfo(name = "idFotoPago") var idFotoPago: Long? = null,
    @ColumnInfo(name = "fechaPago") val fechaPago: String,
    @ColumnInfo(name = "fechaConfirmacion") var fechaConfirmacion: String? = null,
)

fun DbPagoEntity.toDomain() = Pago(
    idPago = idPago,
    idParticipantePago = idParticipantePago,
    idBalance = idBalance,
    idBalancePagado = idBalancePagado,
    monto =monto,
    idFotoPago = idFotoPago,
    fechaPago = fechaPago,
    fechaConfirmacion = fechaConfirmacion
)