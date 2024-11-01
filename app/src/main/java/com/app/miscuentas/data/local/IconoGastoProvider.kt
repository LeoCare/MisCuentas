package com.app.miscuentas.data.local

import com.app.miscuentas.R
import com.app.miscuentas.data.model.IconoGasto

object IconoGastoProvider {

    fun getListIconoGasto(): List<IconoGasto> {
        val imagenGastoList = mutableListOf<IconoGasto>()

        val imagenResIdList = listOf(
            R.drawable.varios,
            R.drawable.supermercado,
            R.drawable.recibo,
            R.drawable.regalo,
            R.drawable.cine,
            R.drawable.coche,
            R.drawable.ropa,
            R.drawable.telefono,
            R.drawable.vacaciones,
            R.drawable.viaje,
            R.drawable.restaurante,
            R.drawable.hogar,
            R.drawable.credito,
            R.drawable.tecnologia,
            R.drawable.wifi,
            R.drawable.mascota
        )

        val nombreImagenes = listOf(
            "Varios", "Super", "Recibo", "Regalo", "Cine", "Coche",
            "Ropa", "Telefono", "Vacaciones", "Viajes", "Restaurante", "Hogar",
            "Credito", "Tecnologia", "Wifi", "Mascotas"
        )

        imagenResIdList.forEachIndexed { index, resId ->
            val imagen: Int = resId
            val id = index + 1
            imagenGastoList.add(IconoGasto(imagen, nombreImagenes[index], id))
        }

        return imagenGastoList
    }
}