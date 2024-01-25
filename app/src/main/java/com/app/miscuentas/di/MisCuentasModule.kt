package com.app.miscuentas.di

import android.content.Context
import androidx.room.Room
import com.app.miscuentas.db.DbMisCuentas
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

//PRUEBA DE SQLITE, BORRAR LUEGO DE IMPLEMENTAR ROOM!!
//    @Singleton
//    @Provides
//    fun provideDbHelper(@ApplicationContext context: Context): DbHelper {
//        return DbHelper(context)
//    }
//
//    @Singleton
//    @Provides
//    fun provideParticipantesDao(dbHelper: DbHelper): ParticipantesDao {
//        return ParticipantesDao(dbHelper)
//    }

    const val DATABASE_NAME = "MisCuentasRoom.db"

    //Usado automaticamente por el sistma!
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