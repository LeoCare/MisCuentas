package com.app.miscuentas.di

import android.content.Context
import androidx.room.Room
import com.app.miscuentas.data.auth.JwtInterceptor
import com.app.miscuentas.data.auth.TokenAuthenticator
import com.app.miscuentas.data.local.datastore.DataStoreConfig
import com.app.miscuentas.data.local.dbroom.DbMisCuentas
import com.app.miscuentas.data.pattern.repository.BalancesRepository
import com.app.miscuentas.data.network.BalancesService
import com.app.miscuentas.data.pattern.repository.GastosRepository
import com.app.miscuentas.data.network.GastosService
import com.app.miscuentas.data.pattern.repository.HojasRepository
import com.app.miscuentas.data.network.HojasService
import com.app.miscuentas.data.pattern.repository.ImagenesRepository
import com.app.miscuentas.data.network.ImagenesService
import com.app.miscuentas.data.pattern.repository.PagosRepository
import com.app.miscuentas.data.network.PagosService
import com.app.miscuentas.data.pattern.repository.ParticipantesRepository
import com.app.miscuentas.data.network.ParticipantesService
import com.app.miscuentas.data.pattern.repository.UsuariosRepository
import com.app.miscuentas.data.network.UsuariosService
import com.app.miscuentas.data.pattern.dao.DbBalanceDao
import com.app.miscuentas.data.pattern.dao.DbImagenDao
import com.app.miscuentas.data.pattern.dao.DbGastoDao
import com.app.miscuentas.data.pattern.dao.DbHojaCalculoDao
import com.app.miscuentas.data.pattern.dao.DbPagoDao
import com.app.miscuentas.data.pattern.dao.DbParticipantesDao
import com.app.miscuentas.data.pattern.dao.DbUsuarioDao
import com.app.miscuentas.data.pattern.webservices.WebService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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

    /***** ROOM *****/
    /****************/
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

    @Singleton
    @Provides
    fun provideDeudaDao(dbMisCuentas: DbMisCuentas) = dbMisCuentas.getDeudaDao()

    @Singleton
    @Provides
    fun providePagoDao(dbMisCuentas: DbMisCuentas) = dbMisCuentas.getPagoDao()

    @Singleton
    @Provides
    fun provideFotoDao(dbMisCuentas: DbMisCuentas) = dbMisCuentas.getFotoDao()
    /*****************/

    /** PREFERENCE DATASTORE DI **/
    /*****************************/
    @Provides
    @Singleton
    fun provideDataStorePref(@ApplicationContext context: Context): DataStoreConfig =
        DataStoreConfig(context)
    /*****************/

    /** AUTENTICACION DI **/
    /**********************/
    @Provides
    @Singleton
    fun provideTokenAuthenticator(dataStoreConfig: DataStoreConfig): TokenAuthenticator {
        return TokenAuthenticator(dataStoreConfig)
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
    /********************/
    @Provides
    @Singleton
    fun provideWebService(okHttpClient: OkHttpClient): WebService {
        return Retrofit.Builder()
            .baseUrl("https://api-miscuentas.leondev.es/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient) // Usa el cliente que tiene el interceptor
            .build()
            .create(WebService::class.java)
    }

    /** USUARIO DI */
    @Provides
    @Singleton
    fun provideUsuariosRepository(webService: WebService, tokenAuthenticator: TokenAuthenticator): UsuariosRepository {
        return UsuariosRepository(webService, tokenAuthenticator)
    }

    @Provides
    @Singleton
    fun provideUsuariosService(usuarioDao: DbUsuarioDao, usuariosRepository: UsuariosRepository): UsuariosService {
        return UsuariosService(usuarioDao, usuariosRepository )
    }

    /*****************/


    /** BALANCE DI */
    @Provides
    @Singleton
    fun provideBalancesRepository(webService: WebService, tokenAuthenticator: TokenAuthenticator): BalancesRepository {
        return BalancesRepository(webService, tokenAuthenticator)
    }

    @Provides
    @Singleton
    fun provideBalancesService(balanceDao: DbBalanceDao, balancesRepository: BalancesRepository): BalancesService {
        return BalancesService(balanceDao, balancesRepository )
    }
    /*****************/

    /** PARTICIPANTES DI */
    @Provides
    @Singleton
    fun provideParticipantesRepository(webService: WebService, tokenAuthenticator: TokenAuthenticator): ParticipantesRepository {
        return ParticipantesRepository(webService, tokenAuthenticator)
    }

    @Provides
    @Singleton
    fun provideParticipantesService(participantesDao: DbParticipantesDao, participantesRepository: ParticipantesRepository): ParticipantesService {
        return ParticipantesService(participantesDao, participantesRepository)
    }

    /** PAGOS DI */
    @Provides
    @Singleton
    fun providePagosRepository(webService: WebService, tokenAuthenticator: TokenAuthenticator): PagosRepository {
        return PagosRepository(webService, tokenAuthenticator)
    }

    @Provides
    @Singleton
    fun providePagosService(pagoDao: DbPagoDao, pagosRepository: PagosRepository): PagosService {
        return PagosService(pagoDao, pagosRepository)
    }

    /** GASTOS DI */
    @Provides
    @Singleton
    fun provideGastosRepository(webService: WebService, tokenAuthenticator: TokenAuthenticator): GastosRepository {
        return GastosRepository(webService, tokenAuthenticator)
    }

    @Provides
    @Singleton
    fun provideGastosService(gastoDao: DbGastoDao, gastosRepository: GastosRepository): GastosService {
        return GastosService(gastoDao, gastosRepository)
    }

    /** HOJAS DI */
    @Provides
    @Singleton
    fun provideHojasRepository(webService: WebService, tokenAuthenticator: TokenAuthenticator): HojasRepository {
        return HojasRepository(webService, tokenAuthenticator)
    }

    @Provides
    @Singleton
    fun provideHojasService(hojaCalculoDao: DbHojaCalculoDao, hojasRepository: HojasRepository): HojasService {
        return HojasService(hojaCalculoDao, hojasRepository)
    }

    /** IMAGENES DI */
    @Provides
    @Singleton
    fun provideImagenesRepository(webService: WebService, tokenAuthenticator: TokenAuthenticator): ImagenesRepository {
        return ImagenesRepository(webService, tokenAuthenticator)
    }

    @Provides
    @Singleton
    fun provideImagenesService(fotoDao: DbImagenDao, imagenesRepository: ImagenesRepository): ImagenesService {
        return ImagenesService(fotoDao, imagenesRepository)
    }
}
