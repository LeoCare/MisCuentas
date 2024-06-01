package com.app.miscuentas.features.nuevo_gasto

import com.app.miscuentas.data.local.dbroom.relaciones.HojaConParticipantes
import com.app.miscuentas.data.local.dbroom.relaciones.ParticipanteConGastos

data class NuevoGastoState(
    val importe: String = "",
    val idGastoElegido: Long = 1,
    val concepto: String = "",
    val pagador: String = "",
    val idPagador: Long = 0,

    //Valores de la BBDD Room
    val insertOk: Boolean = false,
    val hojaActual: HojaConParticipantes? = null,
    val participanteConGasto: ParticipanteConGastos? = null,
    val lineaHojaLin: Int = 0,
    val maxLineaDetHolaCalculo: Int? = 0,

    //Valores de DataStore
    val idHoja: Long? = 0
)
