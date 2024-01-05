package com.app.miscuentas.ui.nueva_hoja.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEmpty

class NuevaHojaViewModel: ViewModel() {

    //Variables de clase -privadas-
    private val _titulo = MutableStateFlow("")
    private val _participante = MutableStateFlow("")
    private val _listaParticipantes = MutableStateFlow<List<String>>(listOf())
    private val _limiteGasto = MutableStateFlow("")
    private val _fechaCierre = MutableStateFlow("")


    //-publicas-
    val titulo: StateFlow<String> = _titulo
    val participante: StateFlow<String> = _participante
    val listaParticipantes: StateFlow<List<String>> = _listaParticipantes
    val limiteGasto: StateFlow<String> = _limiteGasto
    val fechaCierre: StateFlow<String> = _fechaCierre


    //Metodos (para ser llamadas desde la vista)
    fun onTituloFieldChanged(titulo: String){
        _titulo.value = titulo
    }
    fun onParticipanteFieldChanged(participante: String){
        _participante.value = participante
    }
    fun onLimiteGastoFieldChanged(limiteGasto: String){
        _limiteGasto.value = limiteGasto
    }
    fun onFechaCierreFieldChanged(fechaCierre: String) {
        _fechaCierre.value = fechaCierre
    }




    // Añadir un participante a la lista
    fun addParticipante(participante: String) {
        if (participante.isNotBlank()) {
            _listaParticipantes.value = _listaParticipantes.value + participante
            _participante.value = "" // Resetea el campo después de añadir
        }
    }

    // Quitar un participante a la lista
    fun deleteUltimoParticipante() {
        if (_listaParticipantes.value.isNotEmpty()) {
            _listaParticipantes.value = _listaParticipantes.value.dropLast(1)
        }
    }

    // Total participantes
    fun getTotalParticipantes(): Int{
        return _listaParticipantes.value.size
    }

}