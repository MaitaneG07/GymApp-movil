package com.music.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey

@Entity(
    tableName = "canciones",
    foreignKeys = [ForeignKey(
        entity = Usuario::class,
        parentColumns = ["usuarioId"],
        childColumns = ["usuarioId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Cancion(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val author: String,
    val url: String,
    val usuarioId: Int // Referencia
)