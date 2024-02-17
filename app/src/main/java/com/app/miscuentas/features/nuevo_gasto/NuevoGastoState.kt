package com.app.miscuentas.features.nuevo_gasto

data class NuevoGastoState(
    val importe: String = "",
    val concepto: String = "",
    val pagador: String = "",
    val pagadorElegido: Boolean = true
)
