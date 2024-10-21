package com.app.miscuentas.data.pattern

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.findViewTreeLifecycleOwner
import com.app.miscuentas.data.dto.ParticipanteDto
import com.app.miscuentas.data.local.datastore.DataStoreConfig
import com.app.miscuentas.data.local.dbroom.entitys.DbHojaCalculoEntity
import com.app.miscuentas.data.local.dbroom.entitys.DbUsuariosEntity
import com.app.miscuentas.data.model.dtoToEntityList
import com.app.miscuentas.data.model.toEntity
import com.app.miscuentas.data.model.toEntityList
import com.app.miscuentas.data.network.BalancesService
import com.app.miscuentas.data.network.GastosService
import com.app.miscuentas.data.network.HojasService
import com.app.miscuentas.data.network.PagosService
import com.app.miscuentas.data.network.ParticipantesService
import com.app.miscuentas.data.network.UsuariosService
import com.app.miscuentas.domain.dto.UsuarioDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class DataUpdates(
    private val hojasService: HojasService,
    private val balancesService: BalancesService,
    private val participantesService: ParticipantesService,
    private val pagosService: PagosService,
    private val gastosService: GastosService,
    private val usuariosService: UsuariosService,
    private val dataStoreConfig: DataStoreConfig,
){
    /** Elimina los datos de Room para actualizarlos desde la API **/
    suspend fun limpiarYVolcarLogin(idUsuario: Long) {
        withContext(Dispatchers.IO) {
            try {
                val idUsuarioLogin = dataStoreConfig.getIdRegistroPreference()
                val usuario = usuariosService.getUsuarioByIdApi(idUsuario)

                if (usuario != null && idUsuarioLogin != null) {
                    // Insertar usuario principal en Room
                    usuariosService.cleanInsert(usuario.toEntity())

                    // Volcar hojas propietarias y no propietarias
                    volcarHojas(idUsuarioLogin, idUsuario, "S")
                    volcarHojasNoPropietarias(usuario)
                } else {
                    Log.d(TAG, "No hay datos de usuarios local ni en red.")
                }
            } catch (e: Exception) {
                // Manejo de errores unificado
                Log.d(TAG, "Error al volcar datos: ${e}")
            }
        }
    }

    /** Obtiene datos de las hojas desde la API para luego volcar esos datos en Room **/
    private suspend fun volcarHojas(idUsuarioLogin: Long, idUsuario: Long, propietario: String) = withContext(Dispatchers.IO) {
        hojasService.getHojaByApi("id_usuario", idUsuario.toString())?.forEach { hoja ->
            volcarUsuariosDeParticipantes(hoja.idHoja, idUsuario, idUsuarioLogin)
            volcarParticipantes(hoja.toEntity(), propietario)
            volcarBalances(hoja.toEntity())
        }
    }

    /**  **/
    private suspend fun volcarUsuariosDeParticipantes(idHoja: Long, idUsuario: Long, idUsuarioLogin: Long) {
        participantesService.getParticipantesByAPI("id_hoja", idHoja.toString())?.forEach { participante ->
            if (participante.idUsuario != null && participante.idUsuario != idUsuario && participante.idUsuario != idUsuarioLogin) {
                val usuario = usuariosService.getUsuarioByIdApi(participante.idUsuario)
                usuario?.let {
                    usuariosService.cleanUserAndInsert(it.toEntity())
                }
            }
        }
    }

    private suspend fun volcarParticipantes(hoja: DbHojaCalculoEntity, propietario: String) {
        val participantes = participantesService.getParticipantesByAPI("id_hoja", hoja.idHoja.toString())
        participantes?.let {
            hoja.propietaria = propietario
            hojasService.insertHojaConParticipantes(hoja, it.toEntityList())
            volcarGastosYPagos(it)
        }
    }

    private suspend fun volcarGastosYPagos(participantes: List<ParticipanteDto>) = withContext(Dispatchers.IO) {
        participantes.forEach { participante ->
            gastosService.getGastoByAPI("id_participante", participante.idParticipante.toString())?.let { gastos ->
                gastosService.insertAllGastos(gastos.toEntityList())
            }
            pagosService.getPagosBy("id_participante", participante.idParticipante.toString())?.let { pagos ->
                pagosService.insertAllPagos(pagos.toEntityList())
            }
        }
    }

    private suspend fun volcarBalances(hoja: DbHojaCalculoEntity) = withContext(Dispatchers.IO) {
        balancesService.getBalanceByApi("id_hoja", hoja.idHoja.toString())?.let { balances ->
            balancesService.insertBalancesForHoja(hoja, balances.dtoToEntityList())
        }
    }

    private suspend fun volcarHojasNoPropietarias(usuario: UsuarioDto) = withContext(Dispatchers.IO) {
        val usuariosYHojas = mutableMapOf<UsuarioDto, MutableList<DbHojaCalculoEntity>>()

        participantesService.getParticipantesByAPI("correo", usuario.correo)?.forEach { participante ->
            hojasService.getHojaByIdApi(participante.idHoja)?.toEntity()?.let { hoja ->
                if (hoja.idUsuarioHoja != usuario.idUsuario) {
                    hoja.propietaria = "N"
                    val usuarioPropietario = usuariosService.getUsuarioByIdApi(hoja.idUsuarioHoja)
                    usuarioPropietario?.let { usu ->
                        usuariosYHojas.computeIfAbsent(usu) { mutableListOf() }.add(hoja)
                    }
                }
            }
        }

        usuariosYHojas.forEach { (usuario, hojas) ->
            usuariosService.cleanUserAndInsert(usuario.toEntity())
            hojas.forEach { hoja ->
                volcarParticipantes(hoja, "N")
                volcarBalances(hoja)
            }
        }
    }
}