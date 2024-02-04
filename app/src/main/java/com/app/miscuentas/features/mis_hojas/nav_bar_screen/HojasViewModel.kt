package com.app.miscuentas.features.mis_hojas.nav_bar_screen

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.miscuentas.domain.GetMisHojas
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HojasViewModel @Inject constructor(
    private val getMisHojas: GetMisHojas
): ViewModel(){

    private val _hojasState by lazy { MutableStateFlow(HojasState()) }
    val hojasState: StateFlow<HojasState> = _hojasState

    fun getPhotos(){
        viewModelScope.launch {
            try {
                val hojas = getMisHojas.getPhotos()
                if (hojas != null)  _hojasState.value = _hojasState.value.copy(listaHojas = hojas)
                Log.d(ContentValues.TAG, "llamada a getMisHojas")
            } catch (e: Exception) {
                Log.d(ContentValues.TAG, "getMisHojas excepcion: $e")
            }
        }
    }

    init {
        getPhotos()
    }
}