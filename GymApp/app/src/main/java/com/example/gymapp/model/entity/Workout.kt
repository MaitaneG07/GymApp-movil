package com.example.gymapp.model.entity

import java.io.Serializable

data class Workout (

    var id:String="",
    val nombre:String="",
    val fechaInicio:String="",
    val video:String="",
    val completado:Boolean=false,

    var ejercicio: List<Ejercicio> = emptyList()

        ): Serializable {
    companion object {
        private const val serialVersionUID: Long = 1L
    }
}