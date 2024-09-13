package com.app.miscuentas.data.pattern.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.miscuentas.data.local.dbroom.entitys.DbFotosEntity

@Dao
interface DbImagenDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoto(foto: DbFotosEntity): Long

    @Query("SELECT * FROM t_fotos")
    fun getAllFotos(): List<DbFotosEntity>

    @Query("SELECT * FROM t_fotos WHERE idFoto = :idFoto")
    fun getFoto(idFoto: Long): DbFotosEntity

    @Query("DELETE FROM t_fotos")
    suspend fun clearAllFotos()

}