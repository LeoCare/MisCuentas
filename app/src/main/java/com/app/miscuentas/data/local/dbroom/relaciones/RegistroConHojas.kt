package com.app.miscuentas.data.local.dbroom.relaciones

import androidx.room.Embedded
import androidx.room.Relation
import com.app.miscuentas.data.local.dbroom.entitys.DbHojaCalculoEntity
import com.app.miscuentas.data.local.dbroom.entitys.DbRegistrosEntity

data class RegistroConHojas (
    @Embedded val registro: DbRegistrosEntity,
    @Relation (
        entity = DbHojaCalculoEntity::class,
        parentColumn = "idRegistro",
        entityColumn = "idRegistroHoja"
    )
    val hoja: List<HojaConParticipantes>
)
