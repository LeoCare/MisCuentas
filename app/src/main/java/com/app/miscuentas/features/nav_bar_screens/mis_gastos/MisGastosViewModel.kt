package com.app.miscuentas.features.nav_bar_screens.mis_gastos

import androidx.lifecycle.ViewModel
import com.app.miscuentas.data.local.datastore.DataStoreConfig
import com.app.miscuentas.data.local.repository.GastoRepository
import com.app.miscuentas.data.local.repository.HojaCalculoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class MisGastosViewModel @Inject constructor(
    private val dataStoreConfig: DataStoreConfig,
    private val hojaCalculoRepository: HojaCalculoRepository,
    private val gastoRepository: GastoRepository
): ViewModel(){

    private val _misGastosState = MutableStateFlow(MisGastosState())
    val misGastosState: StateFlow<MisGastosState> = _misGastosState



}