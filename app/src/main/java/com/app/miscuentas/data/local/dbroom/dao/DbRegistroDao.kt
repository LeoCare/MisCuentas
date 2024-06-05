package com.app.miscuentas.data.local.dbroom.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.app.miscuentas.data.local.dbroom.entitys.DbRegistrosEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DbRegistroDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(vararg registro: DbRegistrosEntity)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(registro: DbRegistrosEntity): Long

    @Update
    suspend fun update(registro: DbRegistrosEntity)

    @Delete
    suspend fun delete(registro: DbRegistrosEntity)

    //Obtener usuario registrado
    @Query("SELECT * FROM t_registros WHERE nombre = :nombre AND contrasenna = :contrasenna")
    fun getRegistro(nombre: String, contrasenna: String): Flow<DbRegistrosEntity?>

    //Obtener usuario resgistrado al olvidar la contraseña
    @Query("SELECT * FROM t_registros WHERE correo = :correo")
    fun getRegistroExist(correo: String): Flow<DbRegistrosEntity?>
}