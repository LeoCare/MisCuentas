package com.app.miscuentas.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

//PRUEBA DE SQLITE, BORRAR LUEGO DE IMPLEMENTAR ROOM!!
//class DbHelper(context: Context) : SQLiteOpenHelper(context, DbMisHojas.DATABASE_NOMBRE, null, DbMisHojas.DATABASE_VERSION) {
//
//    override fun onCreate(db: SQLiteDatabase) {
//        db.execSQL(DbMisHojas.CREATE_TPARTICIPANTES)
//    }
//
//    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
//        db.execSQL("DROP TABLE IF EXISTS ${DbMisHojas.TABLA_PARTICIPANTES}")
//        onCreate(db)
//    }
//}