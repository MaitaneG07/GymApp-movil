package com.example.gymapp.model.entity

import java.io.Serializable

data class Entrenador(
    var id: String = "",
    var nombre: String = "",
    var apellido1: String = "",
    var apellido2: String = "",
    var email: String = "",
    var fechaNacimiento: String = "",
    var password: String = ""
): Serializable {
    companion object {
        private const val serialVersionUID: Long = 1L
    }
}
