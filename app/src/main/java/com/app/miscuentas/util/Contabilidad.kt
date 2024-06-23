package com.app.miscuentas.util

import com.app.miscuentas.data.local.dbroom.entitys.DbBalanceEntity
import com.app.miscuentas.data.local.dbroom.entitys.DbGastosEntity
import com.app.miscuentas.data.local.dbroom.relaciones.HojaConParticipantes

class Contabilidad {

    companion object Contable{

        /** METODO QUE SUMA LOS GASTOS DE LA LISTA DE GASTOSENTITY **/
        fun totalGastos(listaGastos: List<DbGastosEntity>?): Double {
            return listaGastos?.sumOf {
                it.importe.replace(",", ".").toDoubleOrNull() ?: 0.0
            } ?: 0.0
        }


        /** METODO QUE SUMA LOS GASTOS POR PARETICIPANTES **/
        fun obtenerParticipantesYSumaGastos(hojaConParticipantes: HojaConParticipantes): Map<String, Double> {
            val resultado = mutableMapOf<String, Double>()

            hojaConParticipantes.participantes.forEach { participanteConGastos ->
                val nombreParticipante = participanteConGastos.participante.nombre
                val sumaGastos = participanteConGastos.gastos.sumOf {
                    it.importe.replace(",", ".").toDoubleOrNull() ?: 0.0
                }
                resultado[nombreParticipante] = sumaGastos
            }

            return resultado
        }


        /** METODO QUE CALCULA EL BALANCE PROVISIONAL SEGUN LA HOJACONPARTICIPANTES **/
        fun calcularBalance(hojaConParticipantes: HojaConParticipantes): Map<String, Double> {
            val totalGastos = hojaConParticipantes.participantes
                .flatMap { it.gastos }
                .sumOf { it.importe.replace(",", ".").toDoubleOrNull() ?: 0.0 }

            val numeroDeParticipantes = hojaConParticipantes.participantes.size
            val gastoPromedio = totalGastos / numeroDeParticipantes

            val balances = mutableMapOf<String, Double>()

            hojaConParticipantes.participantes.forEach { participanteConGastos ->
                val nombreParticipante = participanteConGastos.participante.nombre
                val gastoTotalParticipante = participanteConGastos.gastos.sumOf {
                    it.importe.replace(",", ".").toDoubleOrNull() ?: 0.0
                }
                val balance = gastoTotalParticipante - gastoPromedio
                balances[nombreParticipante] = balance
            }

            return balances
        }

        fun saldarDeuda(balanceActual: DbBalanceEntity){

        }

    }
}