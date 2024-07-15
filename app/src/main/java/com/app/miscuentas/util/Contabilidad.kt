package com.app.miscuentas.util

import com.app.miscuentas.data.local.dbroom.entitys.DbBalanceEntity
import com.app.miscuentas.data.local.dbroom.entitys.DbGastosEntity
import com.app.miscuentas.data.local.dbroom.relaciones.HojaConParticipantes
import java.math.BigDecimal
import java.math.RoundingMode

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

        /** REALIZAR BALANCE E INCERTAR EN T_BALANCE AL CERRAR LA HOJA **/
        fun instanciarBalance(balanceDeuda: Map<String, Double>?, hojaAMostrar: HojaConParticipantes? ): List<DbBalanceEntity>{
            var balances : List<DbBalanceEntity> = listOf()

            balanceDeuda?.forEach { (nombre, monto) ->
                val montoRedondeado = BigDecimal(monto).setScale(2, RoundingMode.HALF_UP).toDouble()
                val idParticipante =
                    hojaAMostrar?.participantes?.firstOrNull {
                        it.participante.nombre == nombre
                    }?.participante?.idParticipante
                val deuda = DbBalanceEntity(
                    idBalance = 0,
                    idHojaBalance = hojaAMostrar?.hoja?.idHoja!!,
                    idParticipanteBalance = idParticipante!!,
                    tipo = if(monto < 0) "D" else if(monto > 0) "A" else "B",
                    monto = montoRedondeado //redondeo y con 2 decimales
                )
                balances = balances + deuda
            }
            return balances
        }

        /** METODO QUE COMPRUEBA SI EL GASTO SUPERA EL LIMITE **/
        fun superaLimite(hojaConParticipantes: HojaConParticipantes?, importeGasto: String): Boolean{
            val gasto = importeGasto.stringToDouble()
            val limite = hojaConParticipantes?.hoja?.limite?.stringToDouble()
            val totalGastos = totalGastosHoja(hojaConParticipantes)

            return if (limite != null) (gasto + totalGastos) > limite
            else false
        }

        /** METODO QUE DEVUELVE EL TOTAL DE GASTOS EN ESE MOMENTO **/
        fun totalGastosHoja(hojaConParticipantes: HojaConParticipantes?): Double{
            var totalGastos = 0.0
            hojaConParticipantes?.participantes?.forEach { participanteConGastos ->
                participanteConGastos.participante.nombre
                val sumaGastos = participanteConGastos.gastos.sumOf {
                    it.importe.stringToDouble()
                }
                totalGastos += sumaGastos
            }
            return totalGastos
        }

        /** METODO QUE REEMPLAZA LA COMA Y PARSEA DOUBLE **/
        fun String.stringToDouble(): Double{
            return this.replace(",", ".").toDoubleOrNull() ?: 0.0
        }
        /** METOD PARA REDONDEAR A DOS DECIMALES **/
        fun Double.redondearADosDecimales(): Double {
            return BigDecimal(this).setScale(2, RoundingMode.HALF_UP).toDouble()
        }
        /** METODO QUE COMPRUEBA SI EL MONTO ES IGUAL A 0,01 **/
        fun Double.esMontoPequeno(): Boolean {
            return (this == -0.01 || this  == 0.01)
        }
    }
}