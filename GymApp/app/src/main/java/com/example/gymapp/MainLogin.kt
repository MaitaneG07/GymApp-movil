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

    private lateinit var cbRemember: CheckBox


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

        // Inicializar las vistas
        usuario = findViewById(R.id.InputEmail)
        password = findViewById(R.id.InputContrasenya)

        cargarDatosGuardados()

        findViewById<Button>(R.id.btnRegistrate).setOnClickListener {
            val intent = Intent(this, MainRegistro::class.java)
            startActivity(intent)
            finish()
        }

        findViewById<Button>(R.id.btnLogin).setOnClickListener {
            iniciarSesion()

        }

    }

    /**
     * Función que gestiona el inicio de sesión del usuario.
     *
     * - Valida que los campos de email y contraseña no estén vacíos.
     * - Realiza una consulta en Firestore para verificar las credenciales.
     * - Si son correctas, muestra un mensaje de bienvenida y abre la actividad principal.
     * - Si son incorrectas o hay un error, muestra un mensaje de advertencia.
     *
     * Requiere:
     * - Una colección "GymEloiteBD" > documento "gym_01" > subcolección "Clientes".
     * - Documentos con campos "email" y "password".
     */
    @SuppressLint("UseKtx")
    private fun iniciarSesion() {
        val email = findViewById<EditText>(R.id.InputEmail).text.toString().trim()
        val password = findViewById<EditText>(R.id.InputContrasenya).text.toString().trim()
        val cbRecordar = findViewById<CheckBox>(R.id.cbRemember) // asegúrate de tener este checkbox en el XML

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val db = FirebaseFirestore.getInstance()

        // 🔍 Buscar CLIENTE
        db.collection("GymElorrietaBD")
            .document("gym_01")
            .collection("Clientes")
            .whereEqualTo("email", email)
            .whereEqualTo("password", password)
            .get()
            .addOnSuccessListener { documents ->
                val cliente = documents.documents.firstOrNull()?.toObject(Cliente::class.java)

                // 🔍 Buscar ENTRENADOR
                db.collection("GymElorrietaBD")
                    .document("gym_01")
                    .collection("Entrenadores")
                    .whereEqualTo("email", email)
                    .whereEqualTo("password", password)
                    .get()
                    .addOnSuccessListener { documents ->
                        val entrenador =
                            documents.documents.firstOrNull()?.toObject(Entrenador::class.java)

                        when {
                            cliente != null -> {
                                // ✅ Guardar datos si se marcó la checkbox
                                if (cbRecordar.isChecked) {
                                    guardarDatos(email, password)
                                } else {
                                    borrarDatos()
                                }

                                // Guardar sesión actual
                                val sharedPref = getSharedPreferences("UserSession", MODE_PRIVATE)
                                with(sharedPref.edit()) {
                                    putString("user_email", email)
                                    putString("user_password", password)
                                    putString("user_role", "cliente")
                                    apply()
                                }

                                Toast.makeText(this, "Bienvenido ${cliente.nombre}", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, HistoricoActivity::class.java).apply {
                                    putExtra("cliente", cliente)
                                })
                                finish()
                            }

                            entrenador != null -> {
                                if (cbRecordar.isChecked) {
                                    guardarDatos(email, password)
                                } else {
                                    borrarDatos()
                                }

                                    Toast.makeText(
                                        this,
                                        "Bienvenido ${entrenador.nombre}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    startActivity(
                                        Intent(
                                            this,
                                            EntrenadorActivity::class.java
                                        ).apply {
                                            putExtra("entrenador", entrenador)
                                        })
                                    finish()
                                val sharedPref = getSharedPreferences("UserSession", MODE_PRIVATE)
                                with(sharedPref.edit()) {
                                    putString("user_email", email)
                                    putString("user_password", password)
                                    putString("user_role", "entrenador")
                                    apply()
                                }

                                Toast.makeText(this, "Bienvenido ${entrenador.nombre}", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, MainPerfilActivity::class.java).apply {
                                    putExtra("entrenador", entrenador)
                                })
                                finish()
                            }

                            else -> {
                                Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                            }
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
        val emailGuardado = prefs.getString("email", "")
        val passGuardada = prefs.getString("password", "")

        if (recordar) {
            findViewById<EditText>(R.id.InputEmail).setText(emailGuardado)
            findViewById<EditText>(R.id.InputContrasenya).setText(passGuardada)
            findViewById<CheckBox>(R.id.cbRemember).isChecked = true
        }
    }

    private fun borrarDatos() {
        val prefs = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
        prefs.edit { clear() }
    }
}



