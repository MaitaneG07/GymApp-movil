package com.example.gymapp.model.entity

import java.io.Serializable

class Serie (
    val id:String?="",
    val nombre:String?="",
    val tiempoDuracion:String?=null,
    val tiempoDescanso:String?= null,
    val completado:Boolean=false,
        ): Serializable {
        companion object {
        private const val serialVersionUID: Long = 1L

    }
}