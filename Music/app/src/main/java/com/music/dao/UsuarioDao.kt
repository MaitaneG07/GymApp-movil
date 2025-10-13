package com.music.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.music.entity.Usuario
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {

    @Insert
    suspend fun insertUsuario(usuario: Usuario): Long

    @Update
    suspend fun updateUsuario(usuario: Usuario)

    @Query("DELETE FROM usuarios WHERE usuarioId = :usuarioId")
    suspend fun deleteUsuarioById(usuarioId: Int): Int

    @Query("SELECT * FROM usuarios WHERE usuarioId = :usuarioId")
    suspend fun getUsuarioById(usuarioId: Int): Usuario?

    @Query("SELECT * FROM usuarios")
    fun getAllUsuarios(): Flow<List<Usuario>>

    @Query("SELECT * FROM usuarios WHERE name = :name AND password = :password")
    suspend fun login(name: String, password: String): Usuario?

    @Query("SELECT * FROM usuarios WHERE name = :name")
    fun getUsuarioByName(name: String) : Usuario
}