package com.app.miscuentas.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter

class Validaciones {


    companion object { //(SINGLETON)

        /** Metodo  que comprueba la sintaxis correcta de los importes **/
        fun isValid(str: String, decimalDigits: Int): Boolean {
            // No permitir el punto decimal sin dígitos a la izquierda
            if (str == ",") return false

            // No permitir ceros a la izquierda si no es un número decimal
            if (str.startsWith("0") && str.length > 1 && !str.startsWith("0,")) return false

            // No permitir más de un punto decimal y solo caracteres numéricos
            if (str.count { it == ',' } > 1 || str.any { it != ',' && !it.isDigit() }) return false

            // No permitir más de 'decimalDigits' dígitos después del punto decimal
            if (str.contains(",") && str.length - str.indexOf(',') > decimalDigits + 1) return false

            return true
        }

        /** FORMATEA UN STRING dd/MM/yyyy a tipo LOCALDATE **/
        fun fechaToDateFormat(fecha: String?): LocalDate?{
            val format = "dd/MM/yyyy"

            return if (fecha != null){
                val formatter = DateTimeFormatter.ofPattern(format)
                LocalDate.parse(fecha, formatter)
            }
            else null
        }

        /** FORMATEA UN LOCALDATE a tipo String dd/MM/yyyy **/
        fun fechaToStringFormat(fecha: LocalDate?): String?{
            val format = "dd/MM/yyyy"

            return if (fecha != null){
                val formatter = DateTimeFormatter.ofPattern(format)
                formatter.format(fecha)
            }
            else null
        }

        fun fechaToStringFormatFoto(fecha: LocalDate?): String?{
            val format = "dd_MM_yyyy"

            return if (fecha != null){
                val formatter = DateTimeFormatter.ofPattern(format)
                formatter.format(fecha)
            }
            else null
        }
    }

}