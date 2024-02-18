package com.app.miscuentas.data.local.dbroom.dbRegistros

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DbRegistroDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(vararg registro: DbRegistrosEntity)

    @Update
    suspend fun update(registro: DbRegistrosEntity)

    @Delete
    suspend fun delete(registro: DbRegistrosEntity)

    //Obtener usuario registrado
    @Query("SELECT * FROM t_registros WHERE nombre = :nombre AND contrasenna = :contrasenna")
    fun getRegistro(nombre: String, contrasenna: String): Flow<DbRegistrosEntity>

    //Obtener usuario resgistrado al olvidar la contraseña
    @Query("SELECT * FROM t_registros WHERE correo = :correo")
    fun getRegistroForUpdate(correo: String): Flow<DbRegistrosEntity>
}