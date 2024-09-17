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
import com.app.miscuentas.domain.dto.HojaCrearDto
import com.app.miscuentas.domain.dto.HojaDto
import com.app.miscuentas.util.Validaciones
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class NuevaHojaViewModel @Inject constructor(
    private val hojasService: HojasService,
    private val usuariosService: UsuariosService,
    private val dataStoreConfig: DataStoreConfig
) : ViewModel() {

    private val _nuevaHojaState = MutableStateFlow(NuevaHojaState())
    val nuevaHojaState: StateFlow<NuevaHojaState> = _nuevaHojaState

    fun onTituloFieldChanged(titulo: String) {
        _nuevaHojaState.value = _nuevaHojaState.value.copy(titulo = titulo)
    }

    fun onParticipanteFieldChanged(participante: String) {
        _nuevaHojaState.value = _nuevaHojaState.value.copy(participante = participante)
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
        val parti = participante.toEntity()
        val updatedList =_nuevaHojaState.value.listaParticipantesEntitys + parti
        _nuevaHojaState.value = _nuevaHojaState.value.copy(
            participante = "",
            listaParticipantesEntitys = updatedList
        )
    }

    fun addParticipanteRegistrado(idRegistrado: Long, participante: Participante) {
        val partiRegistrado = participante.toEntityWithUsuario(idRegistrado)
        val updatedList =_nuevaHojaState.value.listaParticipantesEntitys + partiRegistrado
        _nuevaHojaState.value = _nuevaHojaState.value.copy(
            participanteRegistrado = partiRegistrado,
            listaParticipantesEntitys = updatedList
            )
    }

    fun deleteUltimoParticipante() {
        if (_nuevaHojaState.value.listaParticipantesEntitys.isNotEmpty()) {
            val updatedList = _nuevaHojaState.value.listaParticipantesEntitys.dropLast(1)
            _nuevaHojaState.value = _nuevaHojaState.value.copy(listaParticipantesEntitys = updatedList)
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
                //Insert en API
                val hojaAPI = insertHojaApi(titulo, fechaCreacion, fechaCierre, limite?.toDouble(), status, idUsuarioHoja)

                if(hojaAPI != null){
                    //Insert en Room
                    val insertHoja = insertaHojaRoom(hojaAPI.idHoja, titulo, fechaCreacion, fechaCierre, limite?.toDouble(), status, idUsuarioHoja)
                    onInsertOkFieldChanged(insertHoja)
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
            val hojaApi = hojasService.createHoja(hojaCrearDto)
            if (hojaApi != null){
                result = hojaApi // insert OK
                onInsertAPIOkFieldChanged( true)
            }
        } catch (e: Exception) {
            onInsertAPIOkFieldChanged( false)
            result = null // inserción NOK
        }
        return result
    }


    suspend fun insertaHojaRoom(
        idHoja: Long,
        titulo: String,
        fechaCreacion: String,
        fechaCierre: String?,
        limiteGastos: Double?,
        status: String,
        idUsuario: Long
    ): Boolean {

        val hojaRoom = DbHojaCalculoEntity(idHoja, titulo, fechaCreacion, fechaCierre, limiteGastos.toString(), status, idUsuario) //obtiene hojaEntity
        try{
            instaciaParticipantesConHojas(hojaRoom.idHoja) //instancia lista de participantesEntitys

            nuevaHojaState.value.listaParticipantesEntitys.let {
                insertrHojaConParticipantes(hojaRoom, it)
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
    suspend fun insertrHojaConParticipantes(hoja: DbHojaCalculoEntity, participantes: List<DbParticipantesEntity>) {
        hojasService.insertHojaConParticipantes(hoja, participantes)
    }

    fun getIdRegistroPreference(){
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