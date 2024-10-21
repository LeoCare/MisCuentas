package com.app.miscuentas.features.nuevo_gasto

import com.app.miscuentas.data.local.dbroom.relaciones.HojaConParticipantes
import com.app.miscuentas.data.local.dbroom.relaciones.ParticipanteConGastos

data class NuevoGastoState(
    val idRegistrado: Long = 0,
    val importe: String = "",
    val idGastoElegido: Long = 1,
    val concepto: String = "Varios",
    val pagador: String = "",
    val idPagador: Long = 0,
    val superaLimite: Boolean = false,
    val cierreSesion: Boolean = false,

    //Valores de la BBDD Room
    val insertOk: Boolean = false,
    val hojaActual: HojaConParticipantes? = null,
    val participanteConGasto: ParticipanteConGastos? = null,
    val lineaHojaLin: Int = 0,
    val maxLineaDetHolaCalculo: Int? = 0,

    //Valores desde la API
    val insertAPIOk: Boolean = false,

    //Valores de DataStore
    val idHoja: Long? = 0
)
