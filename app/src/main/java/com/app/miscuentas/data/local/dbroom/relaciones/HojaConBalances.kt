package com.app.miscuentas.data.local.dbroom.relaciones

import androidx.room.Embedded
import androidx.room.Relation
import com.app.miscuentas.data.local.dbroom.entitys.DbBalancesEntity
import com.app.miscuentas.data.local.dbroom.entitys.DbHojaCalculoEntity
//import com.app.miscuentas.data.local.dbroom.entitys.DbHojaParticipantesGastosEntity

data class HojaConBalances(

    @Embedded val hoja: DbHojaCalculoEntity,
    @Relation(
        entity = DbBalancesEntity::class,
        parentColumn = "idHoja",
        entityColumn = "idHojaBalance",
    )
    val balances: List<DbBalancesEntity>
)
