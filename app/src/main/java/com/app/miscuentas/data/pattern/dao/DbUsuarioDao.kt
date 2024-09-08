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
    suspend fun insertUsuarioConParticipante(usuario: DbUsuariosEntity, participante: DbParticipantesEntity) : Long{
        val idUsuario = insert(usuario)
        val participanteEntity = participante.copy(idUsuarioParti = idUsuario)
        insertParticipante(participanteEntity)
        return idUsuario
    }

    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(usuario: DbUsuariosEntity): Long

    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertParticipante(participante: DbParticipantesEntity): Long

    @Update
    suspend fun update(usuario: DbUsuariosEntity)

    @Delete
    suspend fun delete(usuario: DbUsuariosEntity)

    // Método para obtener todos los registros de la tabla
    @Query("SELECT * FROM t_usuarios")
    fun getAll(): Flow<List<DbUsuariosEntity>>

    //Obtener usuario resgistrado al olvidar la contraseña
    @Query("SELECT * FROM t_usuarios WHERE idUsuario = :idUsuario")
    fun getUsuarioWhereId(idUsuario: Long): Flow<DbUsuariosEntity>

    //Obtener usuario registrado segun nombre y contraseña
    @Query("SELECT * FROM t_usuarios WHERE nombre = :nombre AND contrasenna = :contrasenna")
    fun getUsuarioWhereLogin(nombre: String, contrasenna: String): Flow<DbUsuariosEntity?>

    //Obtener usuario resgistrado al olvidar la contraseña
    @Query("SELECT * FROM t_usuarios WHERE correo = :correo")
    fun getUsuarioWhereCorreo(correo: String): Flow<DbUsuariosEntity?>
}
