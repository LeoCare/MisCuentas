package com.app.miscuentas.di

import android.content.Context
import androidx.room.Room
import com.app.miscuentas.data.local.dbroom.DbMisCuentas
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

/***** ROOM *****/
/****************/
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    const val DATABASE_NAME = "MisCuentasRoom.db"

    //Usado automaticamente por el sistema!
    //Metodo que nos devuelve una instancia de la DDBB
    //Al marcarlo como @Provides, le indico a Hilt como dar una instancia de la base de datos
    @Provides
    fun getDbMisCuentas(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, DbMisCuentas::class.java, DATABASE_NAME)
            .fallbackToDestructiveMigration() //Al no especificar una migracion adecuada, permitimos la destruccion ante un cambio en el Schema
            .build()


    @Provides
    fun provideParticipantesDao(dbMisCuentas: DbMisCuentas) = dbMisCuentas.getParticipantesDao()

}

