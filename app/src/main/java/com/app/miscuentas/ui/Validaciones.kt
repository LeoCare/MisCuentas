package com.app.miscuentas.ui

class Validaciones {

    //Metodo  que comprueba la sintaxis correcta de los importes
    companion object { //(SINGLETON)
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
    }

}