package com.app.miscuentas.features.nav_bar_screens.participantes

import androidx.lifecycle.ViewModel
import com.app.miscuentas.data.local.datastore.DataStoreConfig
import com.app.miscuentas.data.local.repository.HojaCalculoRepository
import com.app.miscuentas.data.local.repository.ParticipanteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ParticipantesViewModel @Inject constructor(
    private val dataStoreConfig: DataStoreConfig,
    private val hojaCalculoRepository: HojaCalculoRepository,
    private val participanteRepository: ParticipanteRepository
) : ViewModel(){

    private val _participantesState = MutableStateFlow(ParticipantesState())
    val participantesState: StateFlow<ParticipantesState> = _participantesState
}