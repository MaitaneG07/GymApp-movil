package com.example.gymapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.content.edit
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gymapp.model.entity.Cliente
import com.example.gymapp.model.entity.Entrenador
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase




class MainLogin : BaseActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var usuario: EditText
    private lateinit var password: EditText
    private lateinit var cbRemember: CheckBox  // ← correctamente inicializada

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializar Firestore
        db = Firebase.firestore

        // Inicializar vistas
        usuario = findViewById(R.id.InputEmail)
        password = findViewById(R.id.InputContrasenya)
        cbRemember = findViewById(R.id.cbRemember)  // ← aquí se inicializa

        cargarDatosGuardados()

        findViewById<Button>(R.id.btnRegistrate).setOnClickListener {
            startActivity(Intent(this, MainRegistro::class.java))
            finish()
        }

        findViewById<Button>(R.id.btnLogin).setOnClickListener {
            iniciarSesion()
        }
    }

    @SuppressLint("UseKtx")
    private fun iniciarSesion() {
        val email = usuario.text.toString().trim()
        val pass = password.text.toString().trim()

        if (email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        // 🔹 Primero buscamos en Clientes
        db.collection("GymElorrietaBD")
            .document("gym_01")
            .collection("Clientes")
            .whereEqualTo("email", email)
            .whereEqualTo("password", pass)
            .get()
            .addOnSuccessListener { documents ->
                val cliente = documents.documents.firstOrNull()?.toObject(Cliente::class.java)

                if (cliente != null) {
                    guardarSesion(email, pass, "cliente", cliente.id)
                    if (cbRemember.isChecked) guardarDatos(email, pass) else borrarDatos()
                    Toast.makeText(this, "Bienvenido ${cliente.nombre}", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, HistoricoActivity::class.java).apply {
                        putExtra("cliente", cliente)
                    })
                    finish()
                    return@addOnSuccessListener
                }

                // 🔹 Si no es cliente, buscamos en Entrenadores
                db.collection("GymElorrietaBD")
                    .document("gym_01")
                    .collection("Entrenadores")
                    .whereEqualTo("email", email)
                    .whereEqualTo("password", pass)
                    .get()
                    .addOnSuccessListener { docsEntrenador ->
                        val entrenador = docsEntrenador.documents.firstOrNull()?.toObject(Entrenador::class.java)

                        if (entrenador != null) {
                            // Guardar sesión mínima (solo id, email, rol)
                            guardarSesion(email, pass, "entrenador", entrenador.id)

                            // Guardar datos de recordar sesión si corresponde
                            if (cbRemember.isChecked) guardarDatos(email, pass) else borrarDatos()

                            Toast.makeText(this, "Bienvenido ${entrenador.nombre}", Toast.LENGTH_SHORT).show()

                            // Pasar objeto completo por Intent
                            val intent = Intent(this, EntrenadorActivity::class.java)
                            intent.putExtra("entrenador", entrenador)
                            startActivity(intent)
                            finish()
                        }

                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error al buscar entrenador: ${e.message}", Toast.LENGTH_SHORT).show()
                    }

            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al buscar cliente: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    @SuppressLint("UseKtx")
    private fun guardarSesion(email: String, pass: String, rol: String, userId: String) {
        val sharedPref = getSharedPreferences("UserSession", MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("user_email", email)
            putString("user_password", pass)
            putString("user_role", rol)
            putString("user_id", userId)
            apply()
        }
    }

    @SuppressLint("UseKtx")
    private fun guardarDatos(email: String, password: String) {
        val prefs = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
        with(prefs.edit()) {
            putString("email", email)
            putString("password", password)
            putBoolean("recordar", true)
            apply()
        }
    }

    private fun cargarDatosGuardados() {
        val prefs = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
        val recordar = prefs.getBoolean("recordar", false)
        if (recordar) {
            usuario.setText(prefs.getString("email", ""))
            password.setText(prefs.getString("password", ""))
            cbRemember.isChecked = true
        }
    }

    private fun borrarDatos() {
        val prefs = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
        prefs.edit { clear() }
    }
}
