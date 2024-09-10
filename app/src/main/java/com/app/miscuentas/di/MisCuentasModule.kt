package com.app.miscuentas.di

import android.content.Context
import androidx.room.Room
import com.app.miscuentas.data.auth.JwtInterceptor
import com.app.miscuentas.data.auth.TokenAuthenticator
import com.app.miscuentas.data.local.datastore.DataStoreConfig
import com.app.miscuentas.data.local.dbroom.DbMisCuentas
import com.app.miscuentas.data.pattern.BalancesRepository
import com.app.miscuentas.data.network.BalancesService
import com.app.miscuentas.data.pattern.GastosRepository
import com.app.miscuentas.data.network.GastosService
import com.app.miscuentas.data.pattern.HojasRepository
import com.app.miscuentas.data.network.HojasService
import com.app.miscuentas.data.pattern.ImagenesRepository
import com.app.miscuentas.data.network.ImagenesService
import com.app.miscuentas.data.pattern.PagosRepository
import com.app.miscuentas.data.network.PagosService
import com.app.miscuentas.data.pattern.ParticipantesRepository
import com.app.miscuentas.data.network.ParticipantesService
import com.app.miscuentas.data.pattern.TipoBalanceRepository
import com.app.miscuentas.data.network.TipoBalanceService
import com.app.miscuentas.data.pattern.TipoPerfilRepository
import com.app.miscuentas.data.network.TipoPerfilService
import com.app.miscuentas.data.pattern.TipoStatusRepository
import com.app.miscuentas.data.network.TipoStatusService
import com.app.miscuentas.data.pattern.UsuariosRepository
import com.app.miscuentas.data.network.UsuariosService
import com.app.miscuentas.data.pattern.dao.DbUsuarioDao
import com.app.miscuentas.data.pattern.webservices.WebService
import com.app.miscuentas.domain.GetBalances
import com.app.miscuentas.domain.GetGastos
import com.app.miscuentas.domain.GetHojas
import com.app.miscuentas.domain.GetPagos
import com.app.miscuentas.domain.GetParticipantes
import com.app.miscuentas.domain.GetUsuarios
import com.app.miscuentas.domain.GetImagenes
import com.app.miscuentas.domain.GetTipos
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
    fun provideUsuariosService(webService: WebService, tokenAuthenticator: TokenAuthenticator): UsuariosService {
        return UsuariosService(webService, tokenAuthenticator )
    }

    @Provides
    @Singleton
    fun provideUsuariosRepository(usuarioDao: DbUsuarioDao, usuariosService: UsuariosService): UsuariosRepository {
        return UsuariosRepository(usuarioDao, usuariosService)
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
        return BalancesService(webService, tokenAuthenticator )
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

    /** PARTICIPANTES DI */
    @Provides
    @Singleton
    fun provideParticipantesService(webService: WebService, tokenAuthenticator: TokenAuthenticator): ParticipantesService {
        return ParticipantesService(webService, tokenAuthenticator)
    }

    @Provides
    @Singleton
    fun provideParticipantesRepository(participantesService: ParticipantesService): ParticipantesRepository {
        return ParticipantesRepository(participantesService)
    }

    @Provides
    @Singleton
    fun provideGetParticipantes(participantesRepository: ParticipantesRepository): GetParticipantes {
        return GetParticipantes(participantesRepository)
    }

    /** PAGOS DI */
    @Provides
    @Singleton
    fun providePagosService(webService: WebService, tokenAuthenticator: TokenAuthenticator): PagosService {
        return PagosService(webService, tokenAuthenticator)
    }

    @Provides
    @Singleton
    fun providePagosRepository(pagosService: PagosService): PagosRepository {
        return PagosRepository(pagosService)
    }

    @Provides
    @Singleton
    fun provideGetPagos(pagosRepository: PagosRepository): GetPagos {
        return GetPagos(pagosRepository)
    }

    /** GASTOS DI */
    @Provides
    @Singleton
    fun provideGastosService(webService: WebService, tokenAuthenticator: TokenAuthenticator): GastosService {
        return GastosService(webService, tokenAuthenticator)
    }

    @Provides
    @Singleton
    fun provideGastosRepository(gastosService: GastosService): GastosRepository {
        return GastosRepository(gastosService)
    }

    @Provides
    @Singleton
    fun provideGetGastos(gastosRepository: GastosRepository): GetGastos {
        return GetGastos(gastosRepository)
    }

    /** HOJAS DI */
    @Provides
    @Singleton
    fun provideHojasService(webService: WebService, tokenAuthenticator: TokenAuthenticator): HojasService {
        return HojasService(webService, tokenAuthenticator)
    }

    @Provides
    @Singleton
    fun provideHojasRepository(hojasService: HojasService): HojasRepository {
        return HojasRepository(hojasService)
    }

    @Provides
    @Singleton
    fun provideGetHojas(hojasRepository: HojasRepository): GetHojas {
        return GetHojas(hojasRepository)
    }

    /** IMAGENES DI */
    @Provides
    @Singleton
    fun provideImagenesService(webService: WebService, tokenAuthenticator: TokenAuthenticator): ImagenesService {
        return ImagenesService(webService, tokenAuthenticator)
    }

    @Provides
    @Singleton
    fun provideImagenesRepository(imagenesService: ImagenesService): ImagenesRepository {
        return ImagenesRepository(imagenesService)
    }

    @Provides
    @Singleton
    fun provideGetImagenes(imagenesRepository: ImagenesRepository): GetImagenes {
        return GetImagenes(imagenesRepository)
    }

    /** TIPOS DI (PERFILES, BALANCES, STATUS) */
    @Provides
    @Singleton
    fun provideTipoPerfilesService(webService: WebService, tokenAuthenticator: TokenAuthenticator): TipoPerfilService {
        return TipoPerfilService(webService, tokenAuthenticator)
    }

    @Provides
    @Singleton
    fun provideTipoPerfilesRepository(tipoPerfilesService: TipoPerfilService): TipoPerfilRepository {
        return TipoPerfilRepository(tipoPerfilesService)
    }


    @Provides
    @Singleton
    fun provideTipoBalancesService(webService: WebService, tokenAuthenticator: TokenAuthenticator): TipoBalanceService {
        return TipoBalanceService(webService, tokenAuthenticator)
    }

    @Provides
    @Singleton
    fun provideTipoBalancesRepository(tipoBalancesService: TipoBalanceService): TipoBalanceRepository {
        return TipoBalanceRepository(tipoBalancesService)
    }


    @Provides
    @Singleton
    fun provideTipoStatusService(webService: WebService, tokenAuthenticator: TokenAuthenticator): TipoStatusService {
        return TipoStatusService(webService, tokenAuthenticator)
    }

    @Provides
    @Singleton
    fun provideTipoStatusRepository(tipoStatusService: TipoStatusService): TipoStatusRepository {
        return TipoStatusRepository(tipoStatusService)
    }

    @Provides
    @Singleton
    fun provideGetTipos(tipoPerfilRepository: TipoPerfilRepository, tipoBalanceRepository: TipoBalanceRepository, tipoStatusRepository: TipoStatusRepository): GetTipos {
        return GetTipos( tipoPerfilRepository, tipoBalanceRepository, tipoStatusRepository)
    }
}
