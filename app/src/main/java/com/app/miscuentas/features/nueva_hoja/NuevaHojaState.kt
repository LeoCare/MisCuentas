package com.app.miscuentas.features.nueva_hoja

data class NuevaHojaState(
    val titulo: String = "",
    val participante: String = "",
    val listaParticipantes: List<String> = listOf(),
    val limiteGasto: String = "",
    val fechaCierre: String = "",
    val listDbParticipantes: String = "" //Prueba para mostrar los participantes almacenados en la BBDD //Borrar!!

)