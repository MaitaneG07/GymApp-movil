package com.example.gymapp.model.entity


import java.io.Serializable

data class Workout (

    var id:String="",
    val nombre:String="",
    val video:String="",
    val completado:Boolean=false,
    val ejercicios:List<Ejercicio>,
    val nivel : String
): Serializable {
    companion object {
        private const val serialVersionUID: Long = 1L
    }
}