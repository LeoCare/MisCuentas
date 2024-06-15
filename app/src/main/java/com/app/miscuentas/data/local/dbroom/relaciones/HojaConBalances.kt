package com.app.miscuentas.data.local.dbroom.relaciones

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.app.miscuentas.data.local.dbroom.entitys.DbBalanceEntity
import com.app.miscuentas.data.local.dbroom.entitys.DbGastosEntity
import com.app.miscuentas.data.local.dbroom.entitys.DbHojaCalculoEntity
//import com.app.miscuentas.data.local.dbroom.entitys.DbHojaParticipantesGastosEntity
import com.app.miscuentas.data.local.dbroom.entitys.DbParticipantesEntity

data class HojaConBalances(

    @Embedded val hoja: DbHojaCalculoEntity,
    @Relation(
        entity = DbBalanceEntity::class,
        parentColumn = "idHoja",
        entityColumn = "idHojaBalance",
    )
    val balances: List<DbBalanceEntity>
)
