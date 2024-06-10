package com.app.miscuentas.util

import com.app.miscuentas.data.local.dbroom.entitys.DbGastosEntity
import com.app.miscuentas.data.local.dbroom.relaciones.HojaConParticipantes

class Contabilidad {

    companion object Contable{

        fun totalGastos(listaGastos: List<DbGastosEntity>?): Double {
            return listaGastos?.sumOf {
                it.importe.replace(",", ".").toDoubleOrNull() ?: 0.0
            } ?: 0.0
        }


        fun obtenerParticipantesYSumaGastos(hojaConParticipantes: HojaConParticipantes): Map<String, Double> {
            val resultado = mutableMapOf<String, Double>()

            hojaConParticipantes.participantes.forEach { participanteConGastos ->
                val nombreParticipante = participanteConGastos.participante.nombre
                val sumaGastos = participanteConGastos.gastos.sumOf {
                    it.importe.replace(",", ".").toDoubleOrNull() ?: 0.0
                } ?: 0.0
                resultado[nombreParticipante] = sumaGastos
            }

            return resultado
        }
    }
}