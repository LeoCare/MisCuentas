package com.app.miscuentas.features.nueva_hoja

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.miscuentas.data.local.repository.HojaCalculoRepository
import com.app.miscuentas.domain.model.Participante
import com.app.miscuentas.data.local.repository.ParticipanteRepository
import com.app.miscuentas.domain.model.HojaCalculo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeParseException
import javax.inject.Inject

@HiltViewModel
class NuevaHojaViewModel @Inject constructor(
    private val repositoryParticipante: ParticipanteRepository,
    private val repositoryHojaCalculo: HojaCalculoRepository
) : ViewModel() {

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

    /** METODOS PARA EL STATE DE PARTICIPANTES **/
    fun addParticipante(participante: Participante) {
        val updatedList = _eventoState.value.listaParticipantes + participante
        _eventoState.value = _eventoState.value.copy(
            listaParticipantes = updatedList,
            participante = ""
        )

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


    /** METODOS PARA LOS PARTICIPANTES EN ROOM **/
    //Metodo usado en el boton de agregar participante en las nuevas hojas.
    //Agrega los participantes en la tabla t_participantes de la BBDD.
    fun insertAllParticipante(){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                eventoState.value.listaParticipantes.forEach { participante ->
                    repositoryParticipante.insertAll(participante)
                }
                //Vacio la lista:
                _eventoState.value = _eventoState.value.copy(listaParticipantes = emptyList())
            }
        }
    }

    //Pinta una lista con los participantes almacenados en la BBDD
    fun getAllParticipantesToString(): String {//Borrar si no es necesario!!

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                // Se recolecta el Flow de forma asíncrona
                repositoryParticipante.getAllParticipantes().collect { participantes ->
                    _eventoState.value = _eventoState.value.copy(
                        listDbParticipantes = participantes.joinToString(
                            ", "
                        ) { it.nombre })
                }
            }
        }
        return _eventoState.value.listDbParticipantes
    }

    //Pinta una lista con los participantes del state
    fun getListaParticipatesStateString(): String {//Borrar si no es necesario!!
        var lista = ""
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                eventoState.value.listaParticipantes.forEach { participante ->
                    lista = participante.nombre + ','
                }
            }
        }
        return lista
    }

    /** METODOS PARA LA NUEVA HOJA EN ROOM **/
    fun instanceNuevaHoja(): HojaCalculo {
        val fecha: String? = _eventoState.value.fechaCierre.ifEmpty { null }

        return  HojaCalculo(
            2,
            _eventoState.value.titulo,
            fecha,
            _eventoState.value.limiteGasto.toDoubleOrNull(),
            _eventoState.value.status,
            null
        )
    }

    fun insertAllHojaCalculo() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repositoryHojaCalculo.insertAll(instanceNuevaHoja())
            }
        }
    }

}