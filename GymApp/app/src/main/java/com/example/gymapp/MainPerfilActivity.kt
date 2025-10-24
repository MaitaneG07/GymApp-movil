package com.example.gymapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gymapp.adapter.PerfilAdapter
import com.example.gymapp.model.entity.Cliente
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainPerfilActivity : BaseActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var perfilAdapter: PerfilAdapter
    private val listaClientes = mutableListOf<Cliente>()


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

        // Inicializar Firebase
        db = Firebase.firestore

        // Configurar RecyclerView
        recyclerView = findViewById(R.id.recyclerViewPerfil)
        recyclerView.layoutManager = LinearLayoutManager(this)
        perfilAdapter = PerfilAdapter(listaClientes)
        recyclerView.adapter = perfilAdapter

        // Cargar datos del cliente
        cargarDatosCliente()

        val botonVolver: Button = findViewById(R.id.buttonVolver2)
        botonVolver.setOnClickListener {
            val intent = Intent(this, WorkoutActivity::class.java)
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

    @SuppressLint("NotifyDataSetChanged")
    private fun cargarDatosCliente() {
        // Obtener email y password guardados (por ejemplo, de SharedPreferences o Intent)
        val sharedPref = getSharedPreferences("UserSession", MODE_PRIVATE)
        val email = sharedPref.getString("user_email", "") ?: ""
        val password = sharedPref.getString("user_password", "") ?: ""

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "No hay sesión activa", Toast.LENGTH_SHORT).show()
            return
        }

        // Leer datos de Firestore
        db.collection("GymElorrietaBD")
            .document("gym_01")
            .collection("Clientes")
            .whereEqualTo("email", email)
            .whereEqualTo("password", password)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    // Toma el primer documento encontrado y lo convierte a Cliente
                    val cliente = documents.documents.firstOrNull()?.toObject(Cliente::class.java)

                    if (cliente != null) {
                        listaClientes.clear()
                        listaClientes.add(cliente)
                        perfilAdapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(
                            this@MainPerfilActivity,
                            "Error al cargar los datos",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@MainPerfilActivity,
                        "No se encontraron datos del usuario",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Firebase", "Error al leer datos: ${exception.message}")
                Toast.makeText(
                    this@MainPerfilActivity,
                    "Error: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
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