package com.example.gymapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainPerfilActivity : BaseActivity() {
    @SuppressLint("MissingInflatedId", "UseKtx")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_perfil)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val botonVolver: Button = findViewById(R.id.buttonVolver2)
        botonVolver.setOnClickListener {
            val intent = Intent(this, HistoricoActivity::class.java)
            startActivity(intent)
            finish()
        }

        val sharedPref = getSharedPreferences("AppSettings", MODE_PRIVATE)

        // ===== TEMA OSCURO/CLARO =====
        val imageButtonTheme: ImageButton = findViewById(R.id.imageButtonTheme)
        var isDarkMode = sharedPref.getBoolean("dark_mode", false)
        actualizarIconoTema(imageButtonTheme, isDarkMode)

        imageButtonTheme.setOnClickListener {
            isDarkMode = !isDarkMode
            with(sharedPref.edit()) {
                putBoolean("dark_mode", isDarkMode)
                apply()
            }

            if (isDarkMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }

            actualizarIconoTema(imageButtonTheme, isDarkMode)
        }

        // ===== CAMBIO DE IDIOMA =====
        val imageButtonIdioma: ImageButton = findViewById(R.id.imageButtonIdioma)
        var currentLanguage = sharedPref.getString("app_language", "es") ?: "es"

        imageButtonIdioma.setOnClickListener {
            // Alternar entre español e inglés
            currentLanguage = if (currentLanguage == "es") "en" else "es"

            // Guardar preferencia
            with(sharedPref.edit()) {
                putString("app_language", currentLanguage)
                apply()
            }

            // Reiniciar activity para aplicar cambios
            recreate()
        }
    }

    private fun actualizarIconoTema(button: ImageButton, isDarkMode: Boolean) {
        if (isDarkMode) {
            button.setImageResource(R.drawable.soleado)
        } else {
            button.setImageResource(R.drawable.luna)
        }
    }
}