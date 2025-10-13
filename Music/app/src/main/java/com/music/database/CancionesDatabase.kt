package com.music.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.music.dao.CancionDao
import com.music.dao.UsuarioDao
import com.music.entity.Cancion
import com.music.entity.Usuario

@Database(
    entities = [Cancion::class, Usuario::class],
    version = 1,
    exportSchema = false  // Recomendado para evitar problemas de migración
)
abstract class CancionesDatabase : RoomDatabase() {

    abstract fun cancionDao(): CancionDao
    abstract fun usuarioDao(): UsuarioDao

    companion object {
        @Volatile
        private var instance: CancionesDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context): CancionesDatabase = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also { instance = it }
        }

        private fun buildDatabase(context: Context): CancionesDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                CancionesDatabase::class.java,
                "cancionesBBDD"
            ).build()
        }
    }
}