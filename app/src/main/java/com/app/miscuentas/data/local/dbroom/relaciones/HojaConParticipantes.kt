package com.app.miscuentas.data.local.dbroom.relaciones

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.app.miscuentas.data.local.dbroom.entitys.DbGastosEntity
import com.app.miscuentas.data.local.dbroom.entitys.DbHojaCalculoEntity
//import com.app.miscuentas.data.local.dbroom.entitys.DbHojaParticipantesGastosEntity
import com.app.miscuentas.data.local.dbroom.entitys.DbParticipantesEntity

//Relacion entre las entidades DbHojaCalculoEntity y ParticipanteConGastos:
//con esta clase puedo obtener una hoja con participantes y cada participante con sus gastos.

data class HojaConParticipantes(
    //Asi le indico que incluyo DbHojaCalculoEntity en la clase HojaConParticipantes:
    //el objetivo es incluir todas las columnas de DbHojaCalculoEntity como si fueran de HojaConParticipantes!
    @Embedded val hoja: DbHojaCalculoEntity,
    @Relation( //Relacion 'uno a muchos' entre DbHojaCalculoEntity y DbParticipantesEntity
        entity = DbParticipantesEntity::class,
        parentColumn = "idHoja", //indica la columna de la entidad principal (DbHojaCalculoEntity)
        entityColumn = "idHojaParti", //indica la columna, de la entidad a relacionar, que representa a la parentColumn
    )
    //La relacion esta definida para obtener una lista de ParticipanteConGastos, la cual es otra relacion 'uno a muchos'
    val participantes: List<ParticipanteConGastos>
)
