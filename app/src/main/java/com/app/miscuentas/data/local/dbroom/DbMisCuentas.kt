package com.app.miscuentas.data.local.dbroom

import androidx.room.Database
import androidx.room.RoomDatabase
import com.app.miscuentas.data.local.dbroom.dao.DbBalanceDao
import com.app.miscuentas.data.local.dbroom.dao.DbFotoDao
import com.app.miscuentas.data.local.dbroom.dao.DbGastoDao
import com.app.miscuentas.data.local.dbroom.entitys.DbGastosEntity
import com.app.miscuentas.data.local.dbroom.dao.DbHojaCalculoDao
import com.app.miscuentas.data.local.dbroom.dao.DbPagoDao
import com.app.miscuentas.data.local.dbroom.entitys.DbHojaCalculoEntity
import com.app.miscuentas.data.local.dbroom.dao.DbParticipantesDao
import com.app.miscuentas.data.local.dbroom.entitys.DbParticipantesEntity
import com.app.miscuentas.data.local.dbroom.dao.DbRegistroDao
import com.app.miscuentas.data.local.dbroom.entitys.*

const val DATABASE_VERSION = 3
//Instancia de la BBDD
//Proporciona instancia de los DAO
@Database(
    entities = [
        DbParticipantesEntity::class,
        DbRegistrosEntity::class,
        DbHojaCalculoEntity::class,
        DbGastosEntity::class,
        DbBalanceEntity::class,
        DbPagoEntity::class,
        DbFotoEntity::class
    ],
    version = DATABASE_VERSION,
    exportSchema = false
)
abstract class DbMisCuentas : RoomDatabase() {
    abstract fun getParticipantesDao(): DbParticipantesDao //Metodo para instanciar el DAO de Participantes
    abstract fun getRegistroDao(): DbRegistroDao
    abstract fun getHojaCalculoDao(): DbHojaCalculoDao
    abstract fun getGastoDao(): DbGastoDao
    abstract fun getDeudaDao(): DbBalanceDao
    abstract fun getPagoDao(): DbPagoDao
    abstract fun getFotoDao(): DbFotoDao

}