package com.app.miscuentas.data.pattern.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.miscuentas.data.local.dbroom.entitys.DbFotoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DbFotoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoto(foto: DbFotoEntity): Long

    @Query("SELECT * FROM t_fotos")
    fun getAllFotos(): List<DbFotoEntity>

    @Query("SELECT * FROM t_fotos WHERE idFoto = :idFoto")
    fun getFoto(idFoto: Long): DbFotoEntity

}