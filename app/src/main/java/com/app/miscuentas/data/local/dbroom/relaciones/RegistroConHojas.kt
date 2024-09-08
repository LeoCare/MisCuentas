package com.app.miscuentas.data.local.dbroom.relaciones

import androidx.room.Embedded
import androidx.room.Relation
import com.app.miscuentas.data.local.dbroom.entitys.DbHojaCalculoEntity
import com.app.miscuentas.data.local.dbroom.entitys.DbUsuariosEntity

data class RegistroConHojas (
    @Embedded val usuario: DbUsuariosEntity,
    @Relation (
        entity = DbHojaCalculoEntity::class,
        parentColumn = "idUsuario",
        entityColumn = "idUsuarioHoja"
    )
    val hoja: List<HojaConParticipantes>
)
