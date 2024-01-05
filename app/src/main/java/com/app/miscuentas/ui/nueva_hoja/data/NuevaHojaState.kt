package com.app.miscuentas.ui.nueva_hoja.data

data class NuevaHojaState(
    val titulo: String = "",
    val participante: String = "",
    val listaParticipantes: List<String> = listOf(),
    val limiteGasto: String = "",
    val fechaCierre: String = ""

)