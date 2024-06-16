package com.app.miscuentas.util

import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

        /** METODO PARA COMPARTIR LA APP **/
        fun compartirAPP(context: Context, mensajeYRuta: String) {

            // Creación de un Intent para compartir
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                putExtra(Intent.EXTRA_TEXT, mensajeYRuta)
                type = "text/plain"
            }

            // Creación de un Intent para darle al usuario la oportunidad de que elija con qué app compartirá
            val appShareIntent = Intent.createChooser(shareIntent, "POR DONDE COMPARTIR").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            context.startActivity(appShareIntent)
        }

        /** METODO QUE NAVEGA A MI APP EN PLAYSTORE **/
        fun calificarAPP(context: Context) {
            val packageName = "com.bandainamcoent.dblegends_ww" // El paquete de la aplicación en Google Play Store

            try {
                // Intent para abrir la aplicación en Google Play Store
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName"))

                // Verifica si la tienda de Google Play está instalada
                intent.setPackage("com.android.vending")

                // Inicia la actividad
                context.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                // Si la tienda de Google Play no está instalada, abrir en el navegador web
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName"))
                context.startActivity(intent)
            }
        }

        /** METODO PARA ENVIAR UN CORREO **/
        fun envioCorreo(context: Context) {
            val destinatario = "leonardo.care@ciclosmontecastelo.com" // Correo electrónico del destinatario

            // Crear un intent para abrir la aplicación de correo electrónico con el destinatario predefinido
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:$destinatario")
            }

            // Verificar si hay aplicaciones disponibles para manejar el intent
            if (intent.resolveActivity(context.packageManager) != null) {
                // Si hay aplicaciones disponibles, iniciar la actividad con el intent
                context.startActivity(intent)
            } else {
                // Si no hay aplicaciones disponibles, mostrar un mensaje de error
                Toast.makeText(context, "No hay aplicaciones de correo electrónico disponibles", Toast.LENGTH_SHORT).show()
            }
        }

        /** CUADRO DE DIALOGO **/
        //Esta funcion recibe el resultado de cada una de las dos funciones de sus parametros, para ser usados dentro de ella.
        //Al recibir valores lambda, se entiende que el resultado de esas funciones lambda tienen sentido desde donde se llame a MiDialogo().
        //Es decir, dependiendo lo que pase en AlertDialog() ejecutara una lambda u otra (cerrar() o aceptar() )
        @Composable
        fun MiDialogo(
            show:Boolean,
            titulo: String,
            mensaje: String,
            cerrar: () -> Unit,
            aceptar: () -> Unit
        ) {
            if (show) {
                AlertDialog(
                    shape = MaterialTheme.shapes.small,
                    onDismissRequest = { cerrar() },
                    confirmButton = {
                        TextButton(onClick = { aceptar() }) {
                            Text(
                                text = "Aceptar",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { cerrar() }) {
                            Text(
                                text = "Cerrar",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    },
                    title = { Text(
                        text = titulo,
                        style = MaterialTheme.typography.titleLarge
                    ) },
                    text = { Text(
                        text = mensaje,
                        style = MaterialTheme.typography.titleSmall
                    ) }
                )
            }
        }


        @Composable
        fun MiDialogoWithOptions(
            show:Boolean,
            participantes: Map<String, Double>?,
            titulo: String,
            mensaje: String,
            cancelar: () -> Unit,
            onParticipantSelected: (Pair<String, Double>?) -> Unit
        ) {
            if (show) {
                AlertDialog(
                    shape = MaterialTheme.shapes.small,
                    onDismissRequest = { cancelar() },
                    confirmButton = {
                        TextButton(onClick = { cancelar() }) {
                            Text(
                                text = "Cancelar",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    },
                    title = { Text(
                        text = titulo,
                        style = MaterialTheme.typography.titleLarge
                    ) },
                    text = {
                        Column{
                            Text(
                                modifier = Modifier.padding(bottom = 10.dp),
                                text = mensaje,
                                style = MaterialTheme.typography.titleMedium
                            )
                            LazyColumn {
                                participantes?.filter { it.value > 0 }?.let {
                                    items(it.toList()) { (nombre, deuda) ->

                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { onParticipantSelected(Pair(nombre,deuda)) }
                                                .padding(vertical = 8.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(text = nombre, fontSize = 16.sp)
                                            Text(
                                                text = String.format("%.2f €", deuda),
                                                fontSize = 16.sp,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                )
            }
        }

        @Composable
        fun MiAviso(show:Boolean, texto: String, cerrar: () -> Unit) {
            if (show) {
                AlertDialog(
                    shape = MaterialTheme.shapes.small,
                    onDismissRequest = { cerrar() },
                    confirmButton = {
                        TextButton(onClick = { cerrar() }) {
                            Text(
                                text = "Entendido",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    },
                    title = {
                        Text(
                            text = "IMPORTANTE",
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    text = {
                        Text(
                            text = texto,
                            style = MaterialTheme.typography.titleSmall
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            }

        }

    }
}







