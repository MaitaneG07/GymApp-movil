package com.example.gymapp.model.entity

import java.io.Serializable

data class Ejercicio (

     val id: String? = null,
     val nombre: String? = null,
     val descripcion: String? = null,
     val completado: Boolean = false,
     val series: MutableList<Serie?>? = null,
    ) : Serializable
    {
        companion object {
        private const val serialVersionUID: Long = 1L
    }
    }
