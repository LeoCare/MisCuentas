package com.app.miscuentas.di

import android.content.Context
import androidx.room.Room
import com.app.miscuentas.data.local.dbroom.DbMisCuentas
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/***** ROOM *****/
/****************/
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private const val DATABASE_NAME = "MisCuentasRoom.db"

    //Usado automaticamente por el sistema!
    //Metodo que nos devuelve una instancia de la DDBB
    @Singleton
    @Provides
    fun getDbMisCuentas(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, DbMisCuentas::class.java, DATABASE_NAME)
            .fallbackToDestructiveMigration() //Al no especificar una migracion adecuada, permitimos la destruccion ante un cambio en el Schema
            .build()

    @Singleton
    @Provides
    fun provideParticipantesDao(dbMisCuentas: DbMisCuentas) = dbMisCuentas.getParticipantesDao()

    @Singleton
    @Provides
    fun provideRegistroDao(dbMisCuentas: DbMisCuentas) = dbMisCuentas.getRegistroDao()

    @Singleton
    @Provides
    fun provideHojaCalculoDao(dbMisCuentas: DbMisCuentas) = dbMisCuentas.getHojaCalculoDao()

    @Singleton
    @Provides
    fun provideGastoDao(dbMisCuentas: DbMisCuentas) = dbMisCuentas.getGastoDao()

}
