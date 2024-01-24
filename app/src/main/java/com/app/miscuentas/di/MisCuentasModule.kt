package com.app.miscuentas.di

import android.content.Context
import com.app.miscuentas.db.DbHelper
import com.app.miscuentas.db.dao.DbParticipantesDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDbHelper(@ApplicationContext context: Context): DbHelper {
        return DbHelper(context)
    }

    @Singleton
    @Provides
    fun provideParticipantesDao(dbHelper: DbHelper): DbParticipantesDao {
        return DbParticipantesDao(dbHelper)
    }
}