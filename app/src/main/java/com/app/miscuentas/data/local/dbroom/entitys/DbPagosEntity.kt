package com.app.miscuentas.data.local.dbroom.entitys

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "t_pagos",
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
    ]
)
data class DbPagoEntity(
    @PrimaryKey(autoGenerate = true) val idPago: Long = 0,
    @ColumnInfo(name = "idBalance", index = true) val idBalance: Long,
    @ColumnInfo(name = "idBalancePagado", index = true) val idBalancePagado: Long,
    @ColumnInfo(name = "monto") val monto: Double,
    @ColumnInfo(name = "idFotoPago") var idFotoPago: Long? = null,
    @ColumnInfo(name = "fechaPago") val fechaPago: String,
    @ColumnInfo(name = "fechaConfirmacion") val fechaConfirmacion: String,
)