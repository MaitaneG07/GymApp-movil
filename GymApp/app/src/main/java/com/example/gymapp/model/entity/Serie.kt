package com.example.gymapp.model.entity

import java.io.Serializable

class Serie (
    val id:String?="",
    val nombre:String?="",
    val tiempoDueracion:String?="",
    val tiempoDescanso:String?="",
    val completado:Boolean=false,
    val ejercicio: Ejercicio



        ): Serializable {
        companion object {
        private const val serialVersionUID: Long = 1L

    }
}