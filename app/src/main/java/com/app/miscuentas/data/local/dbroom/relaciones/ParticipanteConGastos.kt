package com.app.miscuentas.data.local.dbroom.relaciones

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.app.miscuentas.data.local.dbroom.entitys.DbGastosEntity
//import com.app.miscuentas.data.local.dbroom.entitys.DbHojaParticipantesGastosEntity
import com.app.miscuentas.data.local.dbroom.entitys.DbParticipantesEntity

data class ParticipanteConGastos(
    @Embedded val participante: DbParticipantesEntity,
    @Relation(
        entity = DbGastosEntity::class,
        parentColumn = "idParticipante", //id de DbParticipantesEntity
        entityColumn = "idParticipanteGasto", //id de DbGastosEntity
    )
    val gastos: List<DbGastosEntity> //relacion 'uno a muchos' que devuelve una lista de gastos
)

