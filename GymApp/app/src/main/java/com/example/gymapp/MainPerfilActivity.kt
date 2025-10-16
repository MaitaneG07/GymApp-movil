package com.example.gymapp

import android.graphics.Color
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainPerfilActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_perfil)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val imageButtonTheme: ImageButton = findViewById(R.id.imageButtonTheme)
        val rootLayout = findViewById<ConstraintLayout>(R.id.main)


        var modoDiurno = true

        imageButtonTheme.setOnClickListener {


            if (!modoDiurno) {


                imageButtonTheme.setImageResource(R.drawable.soleado)
                rootLayout.setBackgroundColor(Color.BLACK) // Fondo negro




            } else {


                imageButtonTheme.setImageResource(R.drawable.luna)
                rootLayout.setBackgroundColor(Color.WHITE) // Fondo blanco (o el que tengas



            }

            modoDiurno = !modoDiurno
        }


    }
    }
