package com.app.miscuentas.data.local.dbroom

import androidx.room.Database
import androidx.room.RoomDatabase
import com.app.miscuentas.data.local.dbroom.dbGastos.DbGastoDao
import com.app.miscuentas.data.local.dbroom.dbGastos.DbGastosEntity
import com.app.miscuentas.data.local.dbroom.dbHojaCalculo.DbHojaCalculoDao
import com.app.miscuentas.data.local.dbroom.dbHojaCalculo.DbHojaCalculoEntity
import com.app.miscuentas.data.local.dbroom.dbHojaCalculo.DbHojaCalculoEntityLin
import com.app.miscuentas.data.local.dbroom.dbHojaCalculo.DbHojaCalculoEntityLinDet
import com.app.miscuentas.data.local.dbroom.dbParticipantes.DbParticipantesDao
import com.app.miscuentas.data.local.dbroom.dbParticipantes.DbParticipantesEntity
import com.app.miscuentas.data.local.dbroom.dbRegistros.DbRegistroDao
import com.app.miscuentas.data.local.dbroom.dbRegistros.DbRegistrosEntity

const val DATABASE_VERSION = 1
//Instancia de la BBDD
//Proporciona instancia de los DAO
//Solo se necesita una intancia de DbParticipantes en toda la App
@Database(
    entities = [
        DbParticipantesEntity::class,
        DbRegistrosEntity::class,
        DbHojaCalculoEntity::class,
        DbHojaCalculoEntityLin::class,
        DbHojaCalculoEntityLinDet::class,
        DbGastosEntity::class
    ],
    version = DATABASE_VERSION,
    exportSchema = false
)
abstract class DbMisCuentas : RoomDatabase() {
    abstract fun getParticipantesDao(): DbParticipantesDao //Metodo para instanciar el DAO de Participantes

    abstract fun getRegistroDao(): DbRegistroDao

    abstract fun getHojaCalculoDao(): DbHojaCalculoDao
    abstract fun getGastoDao(): DbGastoDao

}