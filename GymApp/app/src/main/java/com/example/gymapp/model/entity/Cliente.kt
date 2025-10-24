package com.example.gymapp.model.entity

import java.io.Serializable
import java.util.Objects


    data class Cliente(
        var id: String = "",
        var nombre: String = "",
        var apellido1: String = "",
        var apellido2: String = "",
        var fecha_nacimiento: String = "",
        var email: String = "",
        var password: String = ""
    ) : Serializable {

        companion object {
            private const val serialVersionUID: Long = 1L
        }
    }