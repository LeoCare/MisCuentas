package com.app.miscuentas.data.local.dbroom.dbHojaCalculo

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DbHojaCalculoDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(vararg hojaCalculo: DbHojaCalculoEntity)

    //Como la entidad representa una fila en concreto, si se le pasa la entidad modificada la actualizara en la BBDD
    @Update
    suspend fun update(hojaCalculo: DbHojaCalculoEntity)

    @Delete
    suspend fun delete(hojaCalculo: DbHojaCalculoEntity)

    //Room mantiene el Flow actualizado, por lo que solo se necesita obtener los datos una vez.
    //Luego Room se encarga de notificarnos con cada cambio en los datos
    @Query("SELECT * FROM t_hojas_cab WHERE id = :id")
    fun getHojaCalculo(id: Int): Flow<DbHojaCalculoEntity>

    @Query("SELECT * FROM t_hojas_cab ORDER BY id DESC")
    fun getAllHojasCalculos(): Flow<List<DbHojaCalculoEntity>>
}