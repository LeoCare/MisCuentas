package com.app.miscuentas.data.pattern

import com.app.miscuentas.data.local.dbroom.entitys.DbHojaCalculoEntity
import com.app.miscuentas.data.model.dtoToEntityList
import com.app.miscuentas.data.model.toEntity
import com.app.miscuentas.data.model.toEntityList
import com.app.miscuentas.data.network.BalancesService
import com.app.miscuentas.data.network.GastosService
import com.app.miscuentas.data.network.HojasService
import com.app.miscuentas.data.network.PagosService
import com.app.miscuentas.data.network.ParticipantesService
import com.app.miscuentas.domain.dto.UsuarioDto

class DataUpdates(
    private val hojasService: HojasService,
    private val balancesService: BalancesService,
    private val participantesService: ParticipantesService,
    private val pagosService: PagosService,
    private val gastosService: GastosService,
){

    /** Solo si el login es exitoso desde la API y no desde ROOM: **/
    suspend fun limpiarYVolcarLogin(usuario: UsuarioDto){

        //hojas propietarias:
        volcarHojas(usuario.idUsuario)

        //hojas no propietarias:
        volcarHojasNoPropietarias(usuario.idUsuario)
    }

    /** Metodo que obtiene hojas y balances desde la API par el volcado local (Room) **/
    suspend fun volcarHojas(idUsuario: Long){
        //hojas propietarias:
        val hojas = hojasService.getHojaByApi("id_usuario", idUsuario.toString())
        hojas?.forEach { hoja ->
            //participantes:
            volcarParticipantes(hoja.toEntity())

            //balances:
            val balances = balancesService.getBalanceByApi("id_hoja", hoja.idHoja.toString())
            if (balances != null) {
                balancesService.insertBalancesForHoja(hoja.toEntity(), balances.dtoToEntityList())
            }
        }
    }

    /** Hojas creadas por el usuario logeado **/
    suspend fun volcarParticipantes(hoja: DbHojaCalculoEntity){
        val participantes = participantesService.getParticipantesBy("id_hoja", hoja.idHoja.toString())
        if (participantes != null) {
            //Insert Hojas y Participantes Room
            hojasService.insertHojaConParticipantes(hoja, participantes.toEntityList())

            participantes.forEach{ participante ->
                //gastos:
                val gastos = gastosService.getGastoBy("id_participante", participante.idParticipante.toString())
                if(gastos != null) gastosService.insertAllGastos(gastos.toEntityList())
                //pagos:
                val pagos = pagosService.getPagosBy("id_participante", participante.idParticipante.toString())
                if(pagos != null) pagosService.insertAllPagos(pagos.toEntityList())
            }
        }
    }

    /** Hojas en las que soy solo el participantes **/
    suspend fun volcarHojasNoPropietarias(idUsuario: Long){
        participantesService.getParticipantesBy("id_usuario", idUsuario.toString())?.forEach {
            //hojasNoPropietarias:
            hojasService.getHojaByIdApi(it.idHoja)?.toEntity()?.let { hoja ->
                hoja.propietaria = "N"
                volcarParticipantes(hoja)
            }
        }
    }
}