package com.app.miscuentas.features.nueva_hoja

import com.app.miscuentas.domain.model.Participante

data class NuevaHojaState(
    val titulo: String = "",
    val participante: String = "",
    val listaParticipantes: List<Participante> = listOf(),
    val limiteGasto: String = "",
    val fechaCierre: String = "",
    val status: String = "A",
    val listDbParticipantes: String = "" //Prueba para mostrar los participantes almacenados en la BBDD //Borrar!!

)