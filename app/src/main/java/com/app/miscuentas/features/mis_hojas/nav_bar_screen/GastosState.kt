package com.app.miscuentas.features.mis_hojas.nav_bar_screen

import com.app.miscuentas.data.model.Gasto

data class GastosState (
    val datosGuardados: Boolean = false,
    val listaGastos: List<Gasto> = listOf()
)