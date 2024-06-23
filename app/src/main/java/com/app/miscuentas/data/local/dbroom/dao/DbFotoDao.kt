package com.app.miscuentas.data.local.dbroom.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.miscuentas.data.local.dbroom.entitys.DbFotoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DbFotoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoto(photo: DbFotoEntity): Long

    @Query("SELECT * FROM photos")
    fun getAllPhotos(): Flow<List<DbFotoEntity>>
}