package com.example.gymapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class EntrenadorActivity : AppCompatActivity() {
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