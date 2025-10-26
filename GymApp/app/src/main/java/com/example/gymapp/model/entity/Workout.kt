package com.example.gymapp.model.entity

import java.io.Serializable

data class Workout (

    val id:String="",
    val nombre:String="",
    val fechaInicio:String="",
    val video:String="",
    val completado:Boolean=false,
    val ejercicio:List<Ejercicio>



        ): Serializable {
    companion object {
        private const val serialVersionUID: Long = 1L
    }
}