package com.app.miscuentas.di

import android.content.Context
import com.app.miscuentas.data.local.datastore.DataStoreConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**** DATASTORE *****/
/********************/
@Module
@InstallIn(SingletonComponent::class)
object PreferencesModule {

    @Provides
    @Singleton
    fun provideDataStorePref(@ApplicationContext context: Context): DataStoreConfig =
        DataStoreConfig(context)

}