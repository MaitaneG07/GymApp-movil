package com.example.gymapp.model.entity

import java.io.Serializable

data class Historico (

    val id: String? = null,
    val tiempoTotal: String? = null,
    val tiempoPrevisto: String? = null
    ) : Serializable {
        companion object {
        private const val serialVersionUID: Long = 1L
    }
}