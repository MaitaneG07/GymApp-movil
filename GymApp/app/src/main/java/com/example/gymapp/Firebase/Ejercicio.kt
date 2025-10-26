package com.example.gymapp.Firebase

data class Ejercicio(
    var id: String = "",
    var nombre: String = "",
    var descripcion: String = "",
    var completado: Boolean = false,
    var series: List<Serie> = emptyList(),
    var workout: Workout? = null
)
