package com.app.miscuentas.di

import com.app.miscuentas.data.network.hojas.HojasRepository
import com.app.miscuentas.data.network.hojas.HojasService
import com.app.miscuentas.data.network.webservices.WebService
import com.app.miscuentas.domain.GetMisHojas
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/***** MISHOJAS *****/
/********************/
@Module
@InstallIn(SingletonComponent::class)
object MisHojasModule {

    @Provides
    fun provideWebService(): WebService {
        return Retrofit.Builder()
            .baseUrl("https://android-kotlin-fun-mars-server.appspot.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WebService::class.java)
    }

    @Provides
    fun provideHojasService(webService: WebService): HojasService {
        return HojasService(webService)
    }

    @Provides
    fun provideHojasRepository(hojasService: HojasService): HojasRepository {
        return HojasRepository(hojasService)
    }

    @Provides
    fun provideGetMisHojas(hojasRepository: HojasRepository): GetMisHojas {
        return GetMisHojas(hojasRepository)
    }

}