package com.app.miscuentas.features.mis_hojas.nav_bar_screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class GastosViewModel @Inject constructor(
): ViewModel()
{

    private val _gastosState = MutableStateFlow(GastosState())
    val gastosState: StateFlow<GastosState> = _gastosState

    fun setDatosGuardados(guardado: Boolean){
        _gastosState.value = _gastosState.value.copy( datosGuardados = guardado)
    }

}