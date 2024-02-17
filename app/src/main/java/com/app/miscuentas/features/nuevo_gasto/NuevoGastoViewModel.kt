package com.app.miscuentas.features.nuevo_gasto

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class NuevoGastoViewModel @Inject constructor (

): ViewModel()
{
    val _nuevoGastoState = MutableStateFlow(NuevoGastoState())
    val nuevoGastoState: StateFlow<NuevoGastoState> = _nuevoGastoState
    fun onImporteTextFieldChanged(importe: String){
        _nuevoGastoState.value = _nuevoGastoState.value.copy(importe = importe)
    }
    fun onConceptoTextFieldChanged(concepto: String){
        _nuevoGastoState.value = _nuevoGastoState.value.copy(concepto = concepto)
    }
    fun onPagadorChosen(pagador: String){
        _nuevoGastoState.value = _nuevoGastoState.value.copy(pagador = pagador)
    }
    fun onPagadorRadioChanged(pagadorElegido: Boolean){
        _nuevoGastoState.value = _nuevoGastoState.value.copy(pagadorElegido = pagadorElegido)
    }
}