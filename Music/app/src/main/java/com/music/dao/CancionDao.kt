package com.music.dao

import androidx.room.*
import com.music.entity.Cancion
import kotlinx.coroutines.flow.Flow

@Dao
interface CancionDao {

    @Insert
    suspend fun insertCancion(cancion: Cancion): Long

    @Insert
    suspend fun insertAllCanciones(canciones: List<Cancion>)

    @Query("UPDATE canciones SET title = :nuevoTitulo, author = :nuevoAutor, url = :nuevaUrl WHERE id = :cancionId AND usuarioId = :usuarioId")
    suspend fun updateCancionUsuario(cancionId: Int, usuarioId: Int, nuevoTitulo: String, nuevoAutor: String, nuevaUrl: String): Int

    // Eliminar por ID (solo si pertenece al usuario)
    @Query("DELETE FROM canciones WHERE id = :cancionId AND usuarioId = :usuarioId")
    suspend fun deleteCancionById(cancionId: Int, usuarioId: Int): Int

    // Eliminar todas las canciones de un usuario
    @Query("DELETE FROM canciones WHERE usuarioId = :usuarioId")
    suspend fun deleteAllCancionesByUsuario(usuarioId: Int): Int

    @Query("SELECT * FROM canciones WHERE usuarioId = :usuarioId ORDER BY title ASC")
    fun getCancionesByUsuario(usuarioId: Int): Flow<List<Cancion>>

    @Query("SELECT * FROM canciones WHERE usuarioId = :usuarioId AND title LIKE '%' || :titulo || '%' ")
    suspend fun buscarCancionesPorTitulo(titulo: String, usuarioId: Int): List<Cancion>

    // Buscar canciones por autor (de un usuario específico)
    @Query("SELECT * FROM canciones WHERE usuarioId = :usuarioId AND author LIKE '%' || :autor || '%' ")
    suspend fun buscarCancionesPorAutor(autor: String, usuarioId: Int): List<Cancion>

    // Verificar si una canción existe para un usuario
    @Query("SELECT COUNT(*) FROM canciones WHERE id = :cancionId AND usuarioId = :usuarioId")
    suspend fun existsCancion(cancionId: Int, usuarioId: Int): Int

    // Contar canciones de un usuario
    @Query("SELECT COUNT(*) FROM canciones WHERE usuarioId = :usuarioId")
    suspend fun countCancionesByUsuario(usuarioId: Int): Int
}