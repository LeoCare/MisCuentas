package com.app.miscuentas.features.nueva_hoja

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.miscuentas.domain.model.Participante
import com.app.miscuentas.data.local.repository.ParticipanteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class NuevaHojaViewModel @Inject constructor(
    private val repositoryParticipante: ParticipanteRepository
) : ViewModel() {

    // private val participantesDao: ParticipantesDao //PRUEBA DE SQLITE!!
    private val _eventoState = MutableStateFlow(NuevaHojaState())
    val eventoState: StateFlow<NuevaHojaState> = _eventoState

    fun onTituloFieldChanged(titulo: String) {
        _eventoState.value = _eventoState.value.copy(titulo = titulo)
    }

    fun onParticipanteFieldChanged(participante: String) {
        _eventoState.value = _eventoState.value.copy(participante = participante)
    }

    fun onLimiteGastoFieldChanged(limiteGasto: String) {
        _eventoState.value = _eventoState.value.copy(limiteGasto = limiteGasto)
    }

    fun onFechaCierreFieldChanged(fechaCierre: String) {
        _eventoState.value = _eventoState.value.copy(fechaCierre = fechaCierre)
    }

    fun addParticipante(participante: Participante) {
        val updatedList = _eventoState.value.listaParticipantes + participante
        _eventoState.value = _eventoState.value.copy(listaParticipantes = updatedList, participante = "")

    }

    fun deleteUltimoParticipante() {
        if (_eventoState.value.listaParticipantes.isNotEmpty()) {
            val updatedList = _eventoState.value.listaParticipantes.dropLast(1)
            _eventoState.value = _eventoState.value.copy(listaParticipantes = updatedList)
        }
    }

    fun getTotalParticipantes(): Int {
        return _eventoState.value.listaParticipantes.size
    }

    // ROOM
    //Metodo usado en el boton de agregar participante en las nuevas hojas.
    //Agrega los participantes en la tabla t_participantes de la BBDD.
    fun insertAllParticipante(){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                eventoState.value.listaParticipantes.forEach { participante ->
                    repositoryParticipante.insertAll(participante)
                }
            }
        }
    }

    //Prueba para mostrar los participantes almacenados en la BBDD //Borrar si no es necesario!!
     fun getAllParticipantesToString(): String {

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                // Se recolecta el Flow de forma asíncrona
                repositoryParticipante.getAllParticipantes().collect { participantes ->
                    _eventoState.value = _eventoState.value.copy(listDbParticipantes = participantes.joinToString(", ") { it.nombre })
                }
            }
        }
         return  _eventoState.value.listDbParticipantes
    }

}