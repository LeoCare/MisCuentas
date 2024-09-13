package com.app.miscuentas.features.nav_bar_screens.participantes

import androidx.lifecycle.ViewModel
import com.app.miscuentas.data.local.datastore.DataStoreConfig
import com.app.miscuentas.data.network.HojasService
import com.app.miscuentas.data.network.ParticipantesService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ParticipantesViewModel @Inject constructor(
    private val dataStoreConfig: DataStoreConfig,
    private val hojasService: HojasService,
    private val participantesService: ParticipantesService
) : ViewModel(){

    private val _participantesState = MutableStateFlow(ParticipantesState())
    val participantesState: StateFlow<ParticipantesState> = _participantesState
}