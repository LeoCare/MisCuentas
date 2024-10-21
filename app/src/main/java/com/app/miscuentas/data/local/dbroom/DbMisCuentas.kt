package com.app.miscuentas.data.local.dbroom

import androidx.room.Database
import androidx.room.RoomDatabase
import com.app.miscuentas.data.pattern.dao.DbBalanceDao
import com.app.miscuentas.data.pattern.dao.DbImagenDao
import com.app.miscuentas.data.pattern.dao.DbGastoDao
import com.app.miscuentas.data.local.dbroom.entitys.DbGastosEntity
import com.app.miscuentas.data.pattern.dao.DbHojaCalculoDao
import com.app.miscuentas.data.pattern.dao.DbPagoDao
import com.app.miscuentas.data.local.dbroom.entitys.DbHojaCalculoEntity
import com.app.miscuentas.data.pattern.dao.DbParticipantesDao
import com.app.miscuentas.data.local.dbroom.entitys.DbParticipantesEntity
import com.app.miscuentas.data.pattern.dao.DbUsuarioDao
import com.app.miscuentas.data.local.dbroom.entitys.*

const val DATABASE_VERSION = 2
//Instancia de la BBDD
//Proporciona instancia de los DAO
@Database(
    entities = [
        DbParticipantesEntity::class,
        DbUsuariosEntity::class,
        DbHojaCalculoEntity::class,
        DbGastosEntity::class,
        DbBalancesEntity::class,
        DbPagoEntity::class,
        DbFotosEntity::class
    ],
    version = DATABASE_VERSION,
    exportSchema = false
)
abstract class DbMisCuentas : RoomDatabase() {
    abstract fun getParticipantesDao(): DbParticipantesDao //Metodo para instanciar el DAO de Participantes
    abstract fun getRegistroDao(): DbUsuarioDao
    abstract fun getHojaCalculoDao(): DbHojaCalculoDao
    abstract fun getGastoDao(): DbGastoDao
    abstract fun getDeudaDao(): DbBalanceDao
    abstract fun getPagoDao(): DbPagoDao
    abstract fun getFotoDao(): DbImagenDao
}
