package com.example.gymapp.model.entity


import java.io.Serializable

data class Workout (

var id: String = "",
var nombre: String = "",
var fechaInicio: String = "",
var video: String = "",
var nivel: String = "",
var completado: Boolean = false,
var ejercicios: List<Ejercicio> = emptyList()
)
