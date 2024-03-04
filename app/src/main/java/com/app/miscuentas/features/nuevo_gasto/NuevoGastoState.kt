package com.app.miscuentas.features.nuevo_gasto

import com.app.miscuentas.domain.model.HojaCalculo

data class NuevoGastoState(
    val importe: String = "",
    val concepto: String = "",
    val pagador: String = "",
    val idPagador: Int = 0,
    val pagadorElegido: Boolean = true,

    //Valores de la BBDD Room
    val insertOk: Boolean = false,
    val hojaActual: HojaCalculo? = null,
    val maxLineaHojaLin: Int = 0,
    val maxLineaDetHolaCalculo: Int = 0,

    //Valores de DataStore
    val idHoja: Int? = 0
)
