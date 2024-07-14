package com.app.miscuentas.features.nueva_hoja

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.miscuentas.data.local.datastore.DataStoreConfig
import com.app.miscuentas.data.local.dbroom.entitys.DbHojaCalculoEntity
import com.app.miscuentas.data.local.dbroom.entitys.DbParticipantesEntity
import com.app.miscuentas.data.local.repository.HojaCalculoRepository
import com.app.miscuentas.data.local.repository.ParticipanteRepository
import com.app.miscuentas.data.local.repository.RegistroRepository
import com.app.miscuentas.util.Validaciones
import com.app.miscuentas.domain.model.Participante
import com.app.miscuentas.domain.model.toEntity
import com.app.miscuentas.domain.model.toEntityWithRegistro
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
    private val hojaCalculoRepository: HojaCalculoRepository,
    private val registroRepository: RegistroRepository,
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

    fun onIdRegistroChanged(idRegistro: Long) {
        _nuevaHojaState.value = _nuevaHojaState.value.copy(idRegistro = idRegistro)
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
        val partiRegistrado = participante.toEntityWithRegistro(idRegistrado)
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
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val hoja = instanceNuevaHoja() //obtiene hojaEntity
                instaciaParticipantesConHojas(hoja.idHoja) //instancia lista de participantesEntitys

                nuevaHojaState.value.listaParticipantesEntitys.let {
                    insertrHojaConParticipantes(hoja, it)
                }
            }
        }
    }

    /** METODOS PARA LA NUEVA HOJA EN ROOM **/
    fun instanceNuevaHoja(): DbHojaCalculoEntity {
        return  DbHojaCalculoEntity(
            titulo = _nuevaHojaState.value.titulo,
            fechaCreacion = Validaciones.fechaToStringFormat(LocalDate.now()),
            fechaCierre = _nuevaHojaState.value.fechaCierre.ifEmpty { null },
            limite = _nuevaHojaState.value.limiteGasto.ifEmpty { null },
            status = _nuevaHojaState.value.status,
            idRegistroHoja = _nuevaHojaState.value.idRegistro
        )
    }

    /** INSTANCIA PARTICIPANTES CON IDHOJA **/
    fun instaciaParticipantesConHojas(hojaCalculoId: Long) {
        nuevaHojaState.value.listaParticipantesEntitys.forEach { participante ->
            participante.idHojaParti = hojaCalculoId
        }
    }

    /** METODO PARA INSERTAR LA HOJA Y LOS PARTICIPANTES RELACIONADOS **/
    suspend fun insertrHojaConParticipantes(hoja: DbHojaCalculoEntity, participantes: List<DbParticipantesEntity>) {
        hojaCalculoRepository.insertHojaConParticipantes(hoja, participantes)
        _nuevaHojaState.value = _nuevaHojaState.value.copy(insertOk = true)
    }

    fun getIdRegistroPreference(){
        viewModelScope.launch {
            val idRegistrado = dataStoreConfig.getIdRegistroPreference()
            if (idRegistrado != null) {
                onIdRegistroChanged(idRegistrado)
                //Agrego el primer participante que es el registrado
                registroRepository.getRegistroWhereId(idRegistrado).collect{
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