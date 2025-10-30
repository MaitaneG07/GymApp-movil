package com.example.gymapp.model.entity

import com.google.firebase.firestore.PropertyName
import java.io.Serializable

data class Historico (

    var id: String? = null,
    var nombre : String? = null,
    var nivel : String? = null,

    @get:PropertyName("tiempo_total") @set:PropertyName("tiempo_total")
    var tiempoTotal: String? = null,

    @get:PropertyName("tiempo_previsto") @set:PropertyName("tiempo_previsto")
    var tiempoPrevisto: String? = null,

    @get:PropertyName("fecha_inicio") @set:PropertyName("fecha_inicio")
    var fechaInicio : String? = null,
    var porcentaje : String? = null,
    var video : String? = null
) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 1L
    }
}