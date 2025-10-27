package com.example.gymapp.model.entity

import com.google.firebase.firestore.PropertyName
import java.io.Serializable

data class Historico (

    var id: String? = null,
    var nombre : String? = null,
    var nivel : String? = null,

    @PropertyName("tiempo_total")
    var tiempoTotal: String? = null,

    @PropertyName("tiempo_previsto")
    var tiempoPrevisto: String? = null,

    @PropertyName("fecha_inicio")
    var fechaInicio : String? = null,
    var porcentaje : String? = null,
    var video : String? = null
) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 1L
    }
}