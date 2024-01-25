package com.app.miscuentas.db

import android.content.ContentValues

//PRUEBA CON SQLITE!!

//class ParticipantesDao(private val dbHelper: DbHelper) {
//
//    fun insertParticipante(nombre: String, correo: String = ""): Long {
//        val dbEscritor = dbHelper.writableDatabase
//        val ultimaLinea = selectMax("id_participante",null) + 1
//
//        val valores = ContentValues().apply {
//            put(DbMisHojas.ID_PARTICIPANTE, ultimaLinea)
//            put(DbMisHojas.NOMBRE, nombre)
//            put(DbMisHojas.CORREO, correo)
//        }
//
//        return dbEscritor.insert(
//            DbMisHojas.TABLA_PARTICIPANTES,
//            null,
//            valores
//        ) //en caso de error, retornara -1
//    }
//
//    fun getParticipantes(columna: String): String {
//        val participantes = mutableListOf<String>()
//        val dbLector = dbHelper.readableDatabase
//
//        try {
//            val cursor = dbLector.query(
//                DbMisHojas.TABLA_PARTICIPANTES,
//                arrayOf(columna),
//                null,
//                null,
//                null,
//                null,
//                null
//            )
//
//            while (cursor.moveToNext()) {
//                val nombre = cursor.getString(cursor.getColumnIndexOrThrow(columna))
//                participantes.add(nombre)
//            }
//            cursor.close()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//
//        return participantes.joinToString(", ")
//    }
//
//    fun selectMax(campo: String, condicion: String?): Int {
//        val dbLector = dbHelper.readableDatabase
//        var max = 0
//
//        val queryMax = if (condicion.isNullOrEmpty()) {
//            "SELECT MAX($campo) FROM ${DbMisHojas.TABLA_PARTICIPANTES};"
//        } else {
//            "SELECT MAX($campo) FROM ${DbMisHojas.TABLA_PARTICIPANTES} WHERE $condicion;"
//        }
//
//        val cursorMax = dbLector.rawQuery(queryMax, null)
//
//        if (cursorMax.moveToFirst()) {
//            max = cursorMax.getInt(0) // getInt(0) obtiene el valor de la primera columna del resultado
//        }
//        cursorMax.close()
//
//        return max
//    }
//}