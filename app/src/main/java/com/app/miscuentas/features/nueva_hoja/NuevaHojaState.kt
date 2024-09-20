package com.app.miscuentas.features.nueva_hoja

import com.app.miscuentas.data.local.dbroom.entitys.DbParticipantesEntity
import com.app.miscuentas.data.local.dbroom.relaciones.HojaConParticipantes
import com.app.miscuentas.data.model.Participante

data class NuevaHojaState(
    val titulo: String = "",
    val participante: String = "",
    val participanteRegistrado: DbParticipantesEntity? = null,
    val listaParticipantes: List<Participante> = listOf(),
    val listaParticipantesEntitys: List<DbParticipantesEntity> = listOf(),
    val limiteGasto: String = "",
    val fechaCierre: String = "",
    val status: String = "C",
    val idUsuario: Long = 0,

    //Valores de la BBDD Room
    val insertOk: Boolean = false,
    val maxIdHolaCalculo: Long = 0,
    val maxLineaHolaCalculo: Int = 0,
    val hojaConParticipantes: HojaConParticipantes? = null,

    //Valores API
    val insertAPIOk: Boolean = true
)