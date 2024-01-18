package com.app.miscuentas.ui.nueva_hoja.ui

import androidx.lifecycle.ViewModel
import com.app.miscuentas.ui.nueva_hoja.data.NuevaHojaState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class NuevaHojaViewModel @Inject constructor(): ViewModel() {

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

    fun addParticipante(participante: String) {
        if (participante.isNotBlank()) {
            val updatedList = _eventoState.value.listaParticipantes + participante
            _eventoState.value = _eventoState.value.copy(listaParticipantes = updatedList, participante = "")
        }
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

}