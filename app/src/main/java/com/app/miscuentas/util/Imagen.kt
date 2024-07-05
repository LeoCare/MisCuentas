package com.app.miscuentas.util

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class Imagen {

    companion object { //SINGLETON

        /** CAPTURA DE LA IMAGEN DE LA PANTALLA **/
        fun capturarYEnviar(activity: Activity) {
            // Obtener la vista raíz del diseño actual
            val view = activity.window.decorView.rootView

            // Crear un bitmap de la vista
            val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
            val canvas = android.graphics.Canvas(bitmap)
            view.draw(canvas)

            // Guardar el bitmap en la galería usando MediaStore.createWriteRequest
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "captura_${System.currentTimeMillis()}.png")
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis())
                put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
            }
            val resolver = activity.contentResolver
            val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            uri?.let {
                try {
                    resolver.openOutputStream(it)?.use { outputStream ->
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                    }
                    enviarImagen(activity, it)
                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(activity, "Error al guardar la imagen", Toast.LENGTH_SHORT).show()
                }
            }
        }

        fun enviarImagen(activity: Activity, uri: Uri) {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            activity.startActivity(Intent.createChooser(intent, "Compartir captura de pantalla"))
        }
        /*******************************************************/


        /** IMAGENES DE LA CAMARA Y GALERIA **/
        /** Guardar imagen **/
        //Buffer para la imagen de la galeria al almacenamiento
        fun copyStream(input: InputStream, output: OutputStream) {
            val buffer = ByteArray(1024)
            var length: Int
            while (input.read(buffer).also { length = it } > 0) {
                output.write(buffer, 0, length)
            }
        }

        // Función para guardar la imagen en la galería
        fun saveImageToGallery(context: Context, imageUri: Uri) {
            val contentResolver = context.contentResolver
            val imageCollection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            } else {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }

            val newImageDetails = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "IMG_${System.currentTimeMillis()}.jpg")
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }
            val newImageUri = contentResolver.insert(imageCollection, newImageDetails)

            newImageUri?.let { uri ->
                val inputStream: InputStream? = contentResolver.openInputStream(imageUri)
                val outputStream: OutputStream? = contentResolver.openOutputStream(uri)

                if (inputStream != null && outputStream != null) {
                    copyStream(inputStream, outputStream)
                    inputStream.close()
                    outputStream.close()
                }
            }
        }

        /** Crear archivo para la imagen de la camara **/
        fun Context.createTempPictureUri(
            fileName: String = "IMG_${System.currentTimeMillis()}",
            fileExtension: String = ".jpg"
        ): Uri {
            val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM + "/Camera")
            if (!storageDir.exists()) {
                storageDir.mkdirs()
            }
            val tempFile = File(storageDir, "$fileName$fileExtension")
            return FileProvider.getUriForFile(this, "${packageName}.provider", tempFile)
        }
        /**************************************/

    }
}