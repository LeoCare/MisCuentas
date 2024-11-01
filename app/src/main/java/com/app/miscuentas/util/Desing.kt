package com.app.miscuentas.util

import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material.Divider
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.MaterialTheme

import androidx.compose.material3.Text
import androidx.compose.material3.TextButton

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.app.miscuentas.data.local.dbroom.entitys.DbParticipantesEntity
import com.app.miscuentas.util.Validaciones.Companion.emailCorrecto
import kotlinx.coroutines.Delay
import kotlinx.coroutines.InternalCoroutinesApi
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
                    "$dayOfMonth-${(monthOfYear + 1).toString().padStart(2, '0')}-$year"
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
            opciones: Map<DbParticipantesEntity, Double>?,
            titulo: String,
            mensaje: String,
            cancelar: () -> Unit,
            onOptionSelected: (Pair<DbParticipantesEntity, Double>?) -> Unit
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
                                opciones?.filter { it.value > 0 }?.let {
                                    items(it.toList()) { (clave, valor) ->

                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { onOptionSelected(Pair(clave, valor)) }
                                                .padding(vertical = 8.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(text = clave.nombre, fontSize = 16.sp)
                                            Text(
                                                text = String.format("%.2f €", valor),
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
        fun MiDialogoWithOptions2(
            show:Boolean,
            opciones: List<String>,
            titulo: String,
            mensaje: String,
            cancelar: () -> Unit,
            onOptionSelected: (String) -> Unit
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
                                opciones.let {
                                    items(it) { eleccion ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { onOptionSelected(eleccion) }
                                                .padding(vertical = 8.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = eleccion,
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
        fun MiAviso(show:Boolean, titulo: String, mensaje: String, cerrar: () -> Unit) {
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
                            text = titulo,
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    text = {
                        Text(
                            text = mensaje,
                            style = MaterialTheme.typography.titleSmall
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            }

        }


        @Composable
        fun MiImagenDialog(show:Boolean, imagen: Bitmap, cerrar: () -> Unit) {
            if (show) {
                AlertDialog(
                    onDismissRequest = { cerrar() },
                    confirmButton = {
                        TextButton(onClick = { cerrar() }) {
                            Text(
                                text = "Cerrar",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    },
                    text = {
                        Image(
                            painter = rememberAsyncImagePainter(model =
                            ImageRequest.Builder(LocalContext.current)
                                .data(imagen)
                                .size(coil.size.Size.ORIGINAL) // Set the target size to load the image at.
                                .build()),
                            contentDescription = "Gasto Imagen",
                            contentScale = ContentScale.Fit
                        )
                    }
                )
            }
        }

        @Composable
        fun CorreoElectronicoDialog(
            showDialog: Boolean,
            onDismiss: () -> Unit,
            titulo: String,
            mensaje: String,
            onCorreoIntroducido: (String) -> Unit
        ) {
            if (showDialog) {
                var correo by rememberSaveable { mutableStateOf("") }

                AlertDialog(
                    onDismissRequest = onDismiss,
                    title = {
                        Text(text = titulo)
                    },
                    text = {
                        Column {
                            Text(mensaje)
                            TextField(
                                value = correo,
                                onValueChange = { correo = it },
                                placeholder = { Text(text = "ejemplo@correo.com") },
                                singleLine = true
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            onCorreoIntroducido(correo)
                            onDismiss()
                        }) {
                            Text("Aceptar")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = onDismiss) {
                            Text("Cancelar")
                        }
                    }
                )
            }
        }


        @Composable
        fun RecuperarContrasenaDialog(
            showDialog: Boolean,
            onDismiss: () -> Unit,
            onEnviarCorreo: (String) -> Unit,
            onCodigoIntroducido: (String, String) -> Unit
        ) {
            if (showDialog) {
                var correo by rememberSaveable { mutableStateOf("") }
                var codigo by rememberSaveable { mutableStateOf("") }
                var mensaje by rememberSaveable { mutableStateOf("") } //Mensaje a mostrar

                AlertDialog(
                    onDismissRequest = onDismiss,
                    title = {
                        Text(
                            text = "Recuperar Contraseña",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Column {
                                Text(
                                    text = "Enviar código a...",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = correo,
                                    onValueChange = {
                                        correo = it
                                        mensaje = ""
                                                    },
                                    placeholder = { Text(
                                        text = "ejemplo@correo.com",
                                        style = MaterialTheme.typography.bodyLarge
                                    ) },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = TextFieldDefaults.outlinedTextFieldColors(
                                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                    )
                                )
                            }
                            Divider(thickness = 1.dp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
                            Column {
                                Text(
                                    text = "Ya tengo un código...",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = codigo,
                                    onValueChange = {
                                        codigo = it
                                        mensaje = ""
                                                    },
                                    placeholder = { Text(
                                        text = "Código de 4 dígitos",
                                        style = MaterialTheme.typography.bodyLarge
                                    ) },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    colors = TextFieldDefaults.outlinedTextFieldColors(
                                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                    )
                                )
                            }
                        }
                    },
                    confirmButton = {
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            TextButton(
                                onClick = {
                                    if (!emailCorrecto(correo)) {
                                        mensaje = "La sintaxis del correo no es correcta!"
                                    }
                                    else {
                                        mensaje = "Codigo enviado!"
                                        onEnviarCorreo(correo)
                                    }

                                },
                                enabled = correo.isNotBlank(),
                            ) {
                                Text("Enviar Código", style = MaterialTheme.typography.bodyMedium)
                            }
                            TextButton(
                                onClick = {
                                    onCodigoIntroducido(correo, codigo)
                                    onDismiss()
                                },
                                enabled = codigo.length == 4 && codigo.isDigitsOnly(),
                            ) {
                                Text("Confirmar Código", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    },
                    dismissButton = {


                        Row(
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.fillMaxWidth()
                                .padding(top = 1.dp)
                        ){
                            TextButton(onClick = onDismiss) {
                                Text("Cancelar", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                        if(mensaje.isNotEmpty()){
                            Row(
                                horizontalArrangement = Arrangement.End,
                                modifier = Modifier.fillMaxWidth()
                                    .padding(top = 1.dp)
                            ) {
                                Text(mensaje, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.error)
                            }
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    containerColor = MaterialTheme.colorScheme.surface
                )
            }
        }
    }
}







