package com.app.miscuentas.data.local.dbroom.entitys

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "t_pagos",
    foreignKeys = [
        ForeignKey(
            entity = DbBalanceEntity::class,
            parentColumns = ["idBalance"],
            childColumns = ["idBalance"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DbBalanceEntity::class,
            parentColumns = ["idBalance"],
            childColumns = ["idBalancePagado"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DbPagoEntity(
    @PrimaryKey(autoGenerate = true) val idPago: Long = 0,
    val idBalance: Long,
    val idBalancePagado: Long,
    val monto: Double,
    val fechaPago: String,
    val fechaConfirmacion: String,
)