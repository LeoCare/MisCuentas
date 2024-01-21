package com.app.miscuentas.viewmodel.states

data class NuevaHojaState(
    val titulo: String = "",
    val participante: String = "",
    val listaParticipantes: List<String> = listOf(),
    val limiteGasto: String = "",
    val fechaCierre: String = ""

)