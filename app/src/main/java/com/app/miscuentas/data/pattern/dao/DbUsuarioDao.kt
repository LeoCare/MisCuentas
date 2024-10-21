package com.app.miscuentas.data.pattern.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.app.miscuentas.data.local.dbroom.entitys.DbParticipantesEntity
import com.app.miscuentas.data.local.dbroom.entitys.DbUsuariosEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DbUsuarioDao {

    // Método para insertar una lista de registros
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(registros: List<DbUsuariosEntity>)


    @Transaction
    suspend fun clearInsertUsuarioConParticipante(usuario: DbUsuariosEntity, participante: DbParticipantesEntity) : Long{
        val idUsuario = cleanInsert(usuario)
        val participanteEntity = participante.copy(idUsuarioParti = idUsuario)
        clearAllParticipantes()
        insertParticipante(participanteEntity)
        return idUsuario
    }

    @Transaction
    suspend fun cleanInsert(usuario: DbUsuariosEntity): Long {
        clearAllUsuarios()
        return insert(usuario)
    }

    @Transaction
    suspend fun cleanUserAndInsert(usuario: DbUsuariosEntity): Long {
        delete(usuario)
        return insert(usuario)
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(usuario: DbUsuariosEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertParticipante(participante: DbParticipantesEntity): Long

    @Update
    suspend fun update(usuario: DbUsuariosEntity)

    @Delete
    suspend fun delete(usuario: DbUsuariosEntity)

    @Query("DELETE FROM  t_usuarios")
    suspend fun clearAllUsuarios()

    @Query("DELETE FROM  t_usuarios WHERE idUsuario != :idUsuario")
    suspend fun clearAllUsuariosExcept(idUsuario: Long)

    @Query("DELETE FROM t_participantes")
    suspend fun clearAllParticipantes()

    // Método para obtener todos los registros de la tabla
    @Query("SELECT * FROM t_usuarios")
    fun getAll(): Flow<List<DbUsuariosEntity>>

    //Obtener usuario resgistrado al olvidar la contraseña
    @Query("SELECT * FROM t_usuarios WHERE idUsuario = :idUsuario")
    fun getUsuarioWhereId(idUsuario: Long): Flow<DbUsuariosEntity>

    //Obtener usuario registrado segun nombre y contraseña
    @Query("SELECT * FROM t_usuarios WHERE correo = :correo")
    fun getUsuarioWhereLogin(correo: String): Flow<DbUsuariosEntity?>

    //Obtener usuario resgistrado al olvidar la contraseña
    @Query("SELECT * FROM t_usuarios WHERE correo = :correo")
    fun getUsuarioWhereCorreo(correo: String): Flow<DbUsuariosEntity?>

    @Query("SELECT correo FROM t_usuarios WHERE idUsuario = :idUsuario")
    fun getCorreoWhereId(idUsuario: Long): String
}
