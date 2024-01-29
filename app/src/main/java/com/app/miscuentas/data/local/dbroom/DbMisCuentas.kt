package com.app.miscuentas.data.local.dbroom

import androidx.room.Database
import androidx.room.RoomDatabase
import com.app.miscuentas.data.local.dbroom.dbParticipantes.DbParticipantesDao
import com.app.miscuentas.data.local.dbroom.dbParticipantes.DbParticipantesEntity

const val DATABASE_VERSION = 1
//Instancia de la BBDD
//Proporciona instancia de los DAO
//Solo se necesita una intancia de DbParticipantes en toda la App
@Database(entities = [DbParticipantesEntity::class], version = DATABASE_VERSION, exportSchema = false)
abstract class DbMisCuentas: RoomDatabase() {
    abstract fun getParticipantesDao(): DbParticipantesDao //Metodo para instanciar el DAO de Participantes

}