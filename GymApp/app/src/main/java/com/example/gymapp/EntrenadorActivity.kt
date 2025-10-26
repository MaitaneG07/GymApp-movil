package com.example.gymapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button

class EntrenadorActivity : BaseActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entrenador)

        val botonEntrenador : Button = findViewById(R.id.buttonVolverEn)

        botonEntrenador.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }


    }
}