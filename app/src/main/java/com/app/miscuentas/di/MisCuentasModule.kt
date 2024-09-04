package com.app.miscuentas.di

import com.app.miscuentas.data.network.usuario.UsuariosRepository
import com.app.miscuentas.data.network.usuario.UsuariosService
import com.app.miscuentas.data.network.webservices.WebService
import com.app.miscuentas.domain.GetUsuarios
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
            .baseUrl("https://api.leondev.es/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WebService::class.java)
    }

    @Provides
    fun provideUsuariosService(webService: WebService): UsuariosService {
        return UsuariosService(webService)
    }

    @Provides
    fun provideUsuariosRepository(usuariosService: UsuariosService): UsuariosRepository {
        return UsuariosRepository(usuariosService)
    }

    @Provides
    fun provideGetUsuarios(usuariosRepository: UsuariosRepository): GetUsuarios {
        return GetUsuarios(usuariosRepository)
    }


}