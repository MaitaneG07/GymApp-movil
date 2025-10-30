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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gymapp.adapter.PerfilAdapter
import com.example.gymapp.model.entity.Cliente
import com.example.gymapp.model.entity.Entrenador
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainPerfilActivity : BaseActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var perfilAdapter: PerfilAdapter<*>
    private var usuarioLogeado: Any? = null // Cliente o Entrenador

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

        db = Firebase.firestore
        recyclerView = findViewById(R.id.recyclerViewPerfil)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Obtener usuario logeado desde SharedPreferences
        val sharedPref = getSharedPreferences("UserSession", MODE_PRIVATE)
        val rol = sharedPref.getString("user_role", "cliente")
        val id = sharedPref.getString("user_id", "") ?: ""

        if (rol == "cliente") {
            db.collection("GymElorrietaBD")
                .document("gym_01")
                .collection("Clientes")
                .document(id)
                .get()
                .addOnSuccessListener { doc ->
                    val cliente = doc.toObject(Cliente::class.java)
                    if (cliente != null) {
                        usuarioLogeado = cliente
                        perfilAdapter = PerfilAdapter(cliente)
                        recyclerView.adapter = perfilAdapter
                    }
                }
        } else if (rol == "entrenador") {
            db.collection("GymElorrietaBD")
                .document("gym_01")
                .collection("Entrenadores")
                .document(id)
                .get()
                .addOnSuccessListener { doc ->
                    val entrenador = doc.toObject(Entrenador::class.java)
                    if (entrenador != null) {
                        usuarioLogeado = entrenador
                        perfilAdapter = PerfilAdapter(entrenador)
                        recyclerView.adapter = perfilAdapter
                    }
                }
        }

        // Botón volver
        val botonVolver: Button = findViewById(R.id.buttonVolver2)
        botonVolver.setOnClickListener {
            val intent = if (rol == "entrenador") {
                Intent(this, EntrenadorActivity::class.java).apply {
                    putExtra("entrenador", usuarioLogeado as Entrenador)
                }
            } else {
                Intent(this, HistoricoActivity::class.java)
            }
            startActivity(intent)
            finish()
        }

        // Tema oscuro/idioma (igual que antes)
        val imageButtonTheme: ImageButton = findViewById(R.id.imageButtonTheme)
        var isDarkMode = getSharedPreferences("AppSettings", MODE_PRIVATE)
            .getBoolean("dark_mode", false)
        actualizarIconoTema(imageButtonTheme, isDarkMode)
        imageButtonTheme.setOnClickListener {
            isDarkMode = !isDarkMode
            getSharedPreferences("AppSettings", MODE_PRIVATE).edit().apply {
                putBoolean("dark_mode", isDarkMode)
                apply()
            }
            AppCompatDelegate.setDefaultNightMode(
                if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
            actualizarIconoTema(imageButtonTheme, isDarkMode)
        }

        val imageButtonIdioma: ImageButton = findViewById(R.id.imageButtonIdioma)
        var currentLanguage = getSharedPreferences("AppSettings", MODE_PRIVATE)
            .getString("app_language", "es") ?: "es"
        imageButtonIdioma.setOnClickListener {
            currentLanguage = if (currentLanguage == "es") "en" else "es"
            getSharedPreferences("AppSettings", MODE_PRIVATE).edit().apply {
                putString("app_language", currentLanguage)
                apply()
            }
            recreate()
        }
    }

    private fun actualizarIconoTema(button: ImageButton, isDarkMode: Boolean) {
        button.setImageResource(if (isDarkMode) R.drawable.soleado else R.drawable.luna)
    }
}
