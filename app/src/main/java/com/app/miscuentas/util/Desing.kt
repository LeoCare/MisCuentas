package com.app.miscuentas.util

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Color
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import java.util.Calendar


class Desing {

    companion object { //SINGLETON

        /** CALENDARIO **/
        fun showDatePickerDialog(context: Context, onDateSelected: (String) -> Unit) {
            val calendario = Calendar.getInstance()
            val mYear = calendario.get(Calendar.YEAR)
            val mMonth = calendario.get(Calendar.MONTH)
            val mDay = calendario.get(Calendar.DAY_OF_MONTH)


            //Instancio calendario y establezco como minima fecha el dia actual
            val datePickerDialog = DatePickerDialog(context, { _, year, monthOfYear, dayOfMonth ->
                val selectedDate =
                    "$dayOfMonth/${(monthOfYear + 1).toString().padStart(2, '0')}/$year"
                onDateSelected(selectedDate)
            }, mYear, mMonth, mDay)

            datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
            datePickerDialog.show()

            //Botones positivo/negativo y su color
            datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE)?.setTextColor(Color.BLACK)
            datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE)?.setTextColor(Color.BLACK)
        }
    }
}

/** CUADRO DE DIALOGO **/
//Esta funcion recibe el resultado de cada una de las dos funciones de sus parametros, para ser usados dentro de ella.
//Al recibir valores lambda, se entiende que el resultado de esas funciones lambda tienen sentido desde donde se llame a MiDialogo().
//Es decir, dependiendo lo que pase en AlertDialog() ejecutara una lambda u otra (cerrar() o aceptar() )
@Composable
fun MiDialogo(texto: String, cerrar: () -> Unit, aceptar: () -> Unit) {

    AlertDialog(onDismissRequest = { cerrar() },
        confirmButton = {
            TextButton(onClick = { aceptar() }) {
                Text(text = "Aceptar")
            }
        },
        dismissButton = {
            TextButton(onClick = { cerrar() }) {
                Text(text = "Cerrar")
            }
        },
        title = { Text(text = "Mi Diaologo") },
        text = { Text(text = texto) }
    )
}