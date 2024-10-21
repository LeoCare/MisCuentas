package com.app.miscuentas.features.nueva_hoja

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.miscuentas.data.local.datastore.DataStoreConfig
import com.app.miscuentas.data.local.dbroom.entitys.DbHojaCalculoEntity
import com.app.miscuentas.data.local.dbroom.entitys.DbParticipantesEntity
import com.app.miscuentas.data.model.Participante
import com.app.miscuentas.data.model.toEntity
import com.app.miscuentas.data.model.toEntityWithUsuario
import com.app.miscuentas.data.network.HojasService
import com.app.miscuentas.data.network.UsuariosService
import com.app.miscuentas.data.dto.HojaCrearDto
import com.app.miscuentas.data.dto.HojaDto
import com.app.miscuentas.data.network.ParticipantesService
import com.app.miscuentas.data.dto.ParticipanteCrearDto
import com.app.miscuentas.data.dto.ParticipanteDto
import com.app.miscuentas.util.Validaciones
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class NuevaHojaViewModel @Inject constructor(
    private val hojasService: HojasService,
    private val usuariosService: UsuariosService,
    private val dataStoreConfig: DataStoreConfig,
    private val participantesService: ParticipantesService
) : ViewModel() {

    private val _nuevaHojaState = MutableStateFlow(NuevaHojaState())
    val nuevaHojaState: StateFlow<NuevaHojaState> = _nuevaHojaState

    fun onTituloFieldChanged(titulo: String) {
        _nuevaHojaState.value = _nuevaHojaState.value.copy(titulo = titulo)
    }

    fun onParticipanteFieldChanged(participante: String) {
        _nuevaHojaState.value = _nuevaHojaState.value.copy(participante = participante)
    }

    fun onListParticipantesEntityFieldChanged(listaParticipantes: List<DbParticipantesEntity>) {
        _nuevaHojaState.value = _nuevaHojaState.value.copy(listaParticipantesEntitys = listaParticipantes)
    }
    fun onLimiteGastoFieldChanged(limiteGasto: String) {
        _nuevaHojaState.value = _nuevaHojaState.value.copy(limiteGasto = limiteGasto)
    }

    fun onFechaCierreFieldChanged(fechaCierre: String) {
        _nuevaHojaState.value = _nuevaHojaState.value.copy(fechaCierre = fechaCierre)
    }

    fun onInsertOkFieldChanged(insert: Boolean) {
        _nuevaHojaState.value = _nuevaHojaState.value.copy(insertOk = insert)
        vaciarTextFields() //Vacio estados
    }

    fun onInsertAPIOkFieldChanged(insert: Boolean) {
        _nuevaHojaState.value = _nuevaHojaState.value.copy(insertAPIOk = insert)
    }

    fun onIdRegistroChanged(idUsuario: Long) {
        _nuevaHojaState.value = _nuevaHojaState.value.copy(idUsuario = idUsuario)
    }

    /** METODOS PARA EL STATE DE PARTICIPANTES **/
    fun addParticipate(participante: Participante) {
        val parti = participante.toEntity(0)
        val updatedList =_nuevaHojaState.value.listaParticipantesEntitys + parti
        _nuevaHojaState.value = _nuevaHojaState.value.copy(
            participante = "",
            listaParticipantesEntitys = updatedList
        )
    }

    fun addParticipanteRegistrado(idRegistrado: Long, participante: Participante) {
        val partiRegistrado = participante.toEntityWithUsuario(0, idRegistrado)

        if (partiRegistrado.correo != null) partiRegistrado.tipo = "ONLINE"
        else partiRegistrado.tipo = "LOCAL"

        val updatedList =_nuevaHojaState.value.listaParticipantesEntitys + partiRegistrado
        _nuevaHojaState.value = _nuevaHojaState.value.copy(
            participanteRegistrado = partiRegistrado,
            listaParticipantesEntitys = updatedList
            )
    }

    fun deleteUltimoParticipante() {
        if (_nuevaHojaState.value.listaParticipantesEntitys.isNotEmpty()) {
            if(nuevaHojaState.value.listaParticipantesEntitys.last().correo == null){
                val updatedList = _nuevaHojaState.value.listaParticipantesEntitys.dropLast(1)
                onListParticipantesEntityFieldChanged( updatedList)
            }
        }
    }


   /** LLAMADA A INSERTAR HOJA **/
    fun insertHoja() {
        //Instancia hoja
       val titulo = _nuevaHojaState.value.titulo
       val fechaCreacion = Validaciones.fechaToStringFormat(LocalDate.now())?: LocalDate.now().toString()
       val fechaCierre = _nuevaHojaState.value.fechaCierre.ifEmpty { null }
       val limite = _nuevaHojaState.value.limiteGasto.ifEmpty { null }
       val status = _nuevaHojaState.value.status
       val idUsuarioHoja = _nuevaHojaState.value.idUsuario

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                //Insert desde API
                val hojaAPI = insertHojaApi(titulo, fechaCreacion, fechaCierre, limite?.replace(',','.')?.toDouble(), status, idUsuarioHoja)
                var participantesAPI: ParticipanteDto? =  null

                if(hojaAPI != null){
                    //Instancia participantes
                    instaciaParticipantesConHojas(hojaAPI.idHoja) //instancia lista de participantesEntitys
                    nuevaHojaState.value.listaParticipantesEntitys.forEach { participante ->
                        //Insert participantes desde API
                        participantesAPI = insertParticipantesApi(participante.nombre, participante.correo,  participante.tipo, participante.idHojaParti, participante.idUsuarioParti)

                    }
                    if(participantesAPI != null){
                        //Insert en Room
                        val insertHoja = insertaHojaRoom(hojaAPI.idHoja, titulo, fechaCreacion, fechaCierre, limite?.replace(',','.'), status, idUsuarioHoja)
                        onInsertOkFieldChanged(insertHoja)
                    }
                }
            }
        }
    }

    /** INSERTA HOJA (API) **/
    suspend fun insertHojaApi(
        titulo: String,
        fechaCreacion: String,
        fechaCierre: String?,
        limiteGastos: Double?,
        status: String,
        idUsuario: Long
    ): HojaDto? {

        var result: HojaDto? = null
        val hojaCrearDto = HojaCrearDto(titulo, fechaCreacion, fechaCierre, limiteGastos, status, idUsuario)
        try {
            val hojaApi = hojasService.createHojaApi(hojaCrearDto)
            if (hojaApi != null){
                result = hojaApi // insert OK
            }
        } catch (e: Exception) {
            result = null // inserción NOK
        }
        return result
    }

    /** INSERTA PARTICIPANTES (API) **/
    suspend fun insertParticipantesApi(
        nombre: String,
        correo: String?,
        tipo: String,
        idHoja: Long,
        idUsuario: Long?
    ): ParticipanteDto? {

        var result: ParticipanteDto? = null
        val participanteCrearDto = ParticipanteCrearDto(nombre, correo, tipo, idUsuario, idHoja)
        try {
            val participanteApi = participantesService.createParticipanteAPI(participanteCrearDto)
            if (participanteApi != null){
                result = participanteApi // insert OK
            }
        } catch (e: Exception) {
            result = null // inserción NOK
        }
        return result
    }


    suspend fun insertaHojaRoom(
        idHoja: Long,
        titulo: String,
        fechaCreacion: String,
        fechaCierre: String?,
        limiteGastos: String?,
        status: String,
        idUsuario: Long
    ): Boolean {

        val hojaRoom = DbHojaCalculoEntity(idHoja, titulo, fechaCreacion, fechaCierre, limiteGastos, status, idUsuario) //obtiene hojaEntity
        try{
            nuevaHojaState.value.listaParticipantesEntitys.let {
                insertrHojaConParticipantesRoom(hojaRoom, it)
            }
            return true //insercion OK
        } catch (e: Exception) {
            return false // inserción NOK
        }
    }

    /** INSTANCIA PARTICIPANTES CON IDHOJA **/
    fun instaciaParticipantesConHojas(hojaCalculoId: Long) {
        nuevaHojaState.value.listaParticipantesEntitys.forEach { participante ->
            participante.idHojaParti = hojaCalculoId
        }
    }

    /** METODO PARA INSERTAR LA HOJA Y LOS PARTICIPANTES RELACIONADOS **/
    suspend fun insertrHojaConParticipantesRoom(hoja: DbHojaCalculoEntity, participantes: List<DbParticipantesEntity>) {
        hojasService.insertHojaConParticipantes(hoja, participantes)
    }

    suspend fun getIdRegistroPreference(){
        viewModelScope.launch {
            val idRegistrado = dataStoreConfig.getIdRegistroPreference()
            if (idRegistrado != null) {
                onIdRegistroChanged(idRegistrado)
                //Agrego el primer participante que es el registrado
                usuariosService.getRegistroWhereId(idRegistrado).collect{
                    it?.let { it1 ->
                        addParticipanteRegistrado(idRegistrado, Participante(0, it1.nombre, it.correo))
                    }
                }
            }
        }
    }

   private fun vaciarTextFields(){
       _nuevaHojaState.value = _nuevaHojaState.value.copy(
           titulo = "",
           listaParticipantes = emptyList(),
           limiteGasto = "",
           fechaCierre = "")
   }

}