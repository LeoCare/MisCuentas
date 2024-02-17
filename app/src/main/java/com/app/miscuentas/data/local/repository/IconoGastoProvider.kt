package com.app.miscuentas.data.local.repository

import android.content.Context
import com.app.miscuentas.R
import com.app.miscuentas.domain.model.IconoGasto

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
            "Varios", "Supermercado", "Recibo", "Regalo", "Cine", "Coche",
            "Ropa", "Telefono", "Vacaciones", "Viajes", "Restaurante", "Hogar",
            "Credito", "Tecnologia", "Wifi", "Mascotas"
        )

        imagenResIdList.forEachIndexed { index, resId ->
            val imagen: Int = resId
            imagenGastoList.add(IconoGasto(imagen, nombreImagenes[index], index))
        }

        return imagenGastoList
    }
}