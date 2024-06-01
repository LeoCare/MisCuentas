package com.app.miscuentas.features.nueva_hoja

import com.app.miscuentas.data.local.dbroom.relaciones.HojaConParticipantes
import com.app.miscuentas.domain.model.Participante

data class NuevaHojaState(
    val titulo: String = "",
    val participante: String = "",
    val listaParticipantes: List<Participante> = listOf(),
    val limiteGasto: String = "",
    val fechaCierre: String = "",
    val status: String = "C",
    val listDbParticipantes: String = "", //Prueba para mostrar los participantes almacenados en la BBDD //Borrar!!

    //Valores de la BBDD Room
    val insertOk: Boolean = false,
    val maxIdHolaCalculo: Long = 0,
    val maxLineaHolaCalculo: Int = 0,
    val hojaConParticipantes: HojaConParticipantes? = null
)