package com.app.miscuentas.di

import com.app.miscuentas.data.auth.JwtInterceptor
import com.app.miscuentas.data.auth.TokenAuthenticator
import com.app.miscuentas.data.network.balance.BalancesRepository
import com.app.miscuentas.data.network.balance.BalancesService
import com.app.miscuentas.data.network.usuario.UsuariosRepository
import com.app.miscuentas.data.network.usuario.UsuariosService
import com.app.miscuentas.data.network.webservices.WebService
import com.app.miscuentas.domain.GetBalances
import com.app.miscuentas.domain.GetUsuarios
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/***** MISHOJAS *****/
/********************/
@Module
@InstallIn(SingletonComponent::class)
object MisHojasModule {

    /** AUTENTICACION DI **/
    @Provides
    @Singleton
    fun provideTokenAuthenticator(): TokenAuthenticator {
        return TokenAuthenticator()
    }

    @Provides
    @Singleton
    fun provideJwtInterceptor(tokenAuthenticator: TokenAuthenticator): JwtInterceptor {
        return JwtInterceptor(tokenAuthenticator)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(jwtInterceptor: JwtInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(jwtInterceptor)
            .build()
    }
    /*****************/


    /** WEBSERVICES DI **/
    @Provides
    @Singleton
    fun provideWebService(okHttpClient: OkHttpClient): WebService {
        return Retrofit.Builder()
            .baseUrl("https://api.leondev.es/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient) // Usa el cliente que tiene el interceptor
            .build()
            .create(WebService::class.java)
    }
    /*****************/


    /** USUARIO DI */
    @Provides
    @Singleton
    fun provideUsuariosService(webService: WebService, tokenAuthenticator: TokenAuthenticator): UsuariosService {
        return UsuariosService(webService, tokenAuthenticator )
    }

    @Provides
    @Singleton
    fun provideUsuariosRepository(usuariosService: UsuariosService): UsuariosRepository {
        return UsuariosRepository(usuariosService)
    }

    @Provides
    @Singleton
    fun provideGetUsuarios(usuariosRepository: UsuariosRepository): GetUsuarios {
        return GetUsuarios(usuariosRepository)
    }
    /*****************/


    /** BALANCE DI */
    @Provides
    @Singleton
    fun provideBalancesService(webService: WebService, tokenAuthenticator: TokenAuthenticator): BalancesService {
        return BalancesService(webService)
    }

    @Provides
    @Singleton
    fun provideBalancesRepository(balancesService: BalancesService): BalancesRepository {
        return BalancesRepository(balancesService)
    }

    @Provides
    @Singleton
    fun provideGetBalances(balancesRepository: BalancesRepository): GetBalances {
        return GetBalances(balancesRepository)
    }
    /*****************/


}