package com.app.miscuentas.data.local.dbroom.entitys

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "t_balance",
    foreignKeys = [
        ForeignKey(
            entity = DbHojaCalculoEntity::class,
            parentColumns = ["idHoja"],
            childColumns = ["idHojaBalance"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DbParticipantesEntity::class,
            parentColumns = ["idParticipante"],
            childColumns = ["idParticipanteBalance"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DbBalanceEntity(
    @PrimaryKey(autoGenerate = true) val idBalance: Long = 0,
    val idHojaBalance: Long,
    val idParticipanteBalance: Long,
    var tipo: String,
    var monto: Double
)