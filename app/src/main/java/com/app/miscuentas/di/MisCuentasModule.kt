package com.app.miscuentas.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.app.miscuentas.db.DbMisCuentas
import com.app.miscuentas.repository.DataStoreConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.Serializable
import javax.inject.Inject
import javax.inject.Singleton

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

@Module
@InstallIn(SingletonComponent::class)
object PreferencesModule {
    //PRUEBA DE SHAREDPREFERENCE, BORRAR!!
//    @Provides
//    @Singleton
//    fun provideSharedPreferences(@ApplicationContext appContext: Context): SharedPreferences {
//        return appContext.getSharedPreferences("mis_cuentas_preferences", Context.MODE_PRIVATE)
//    }

    //PRUEBA CON DATASTORE
    @Provides
    @Singleton
    fun provideDataStorePref(@ApplicationContext context: Context): DataStoreConfig =
        DataStoreConfig(context)

}
