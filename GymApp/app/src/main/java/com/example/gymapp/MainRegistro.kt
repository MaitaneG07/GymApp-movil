package com.example.gymapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class MainRegistro : BaseActivity() {

    //variables que se usaran mas tarde pero nunca pueden ser nulas
    private lateinit var nombre: EditText
    private lateinit var apellido1: EditText
    private lateinit var apellido2: EditText
    private lateinit var fechaNacimiento: EditText
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var btnRegistrar: Button
    private lateinit var db: FirebaseFirestore

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_registro)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<Button>(R.id.buttonVolver).setOnClickListener {
            val intent = Intent(this, MainLogin::class.java)
            startActivity(intent)
            finish()
        }

        // 🔥 Inicializar Firestore
        db = Firebase.firestore

        // 🔹 Inicializar vistas
        nombre = findViewById(R.id.editTextNombre)
        apellido1 = findViewById(R.id.editTextApellido1)
        apellido2 = findViewById(R.id.editTextApellido2)
        fechaNacimiento = findViewById(R.id.editTextFecha)
        email = findViewById(R.id.InputemailRegistro)
        password = findViewById(R.id.InputPassword)
        btnRegistrar = findViewById(R.id.btnRegistrar)

        // 🔹 Inicializar Spinner
        val spinner = findViewById<Spinner>(R.id.spinnerCli_entre)
        val opciones = listOf("Entrenador", "Cliente")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, opciones)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        // 🔹 Evento del botón
        btnRegistrar.setOnClickListener {
            lifecycleScope.launch {
                val tipoSeleccionado = spinner.selectedItem.toString()

                // 🔹 Obtenemos el siguiente ID según si es Cliente o Entrenador
                val siguienteId = obtenerSiguienteId(
                    coleccionPadre = "GymElorrietaBD",
                    documentoGym = "gym_01",
                    subcoleccion = if (tipoSeleccionado == "Entrenador") "Entrenadores" else "Clientes"
                )

                registrarUsuario(tipoSeleccionado, siguienteId)
            }
        }
    }

    private fun registrarUsuario(tipoSeleccionado: String, nuevoId: String) {
        val nombreText = nombre.text.toString().trim()
        val apellido1Text = apellido1.text.toString().trim()
        val apellido2Text = apellido2.text.toString().trim()
        val fechaText = fechaNacimiento.text.toString().trim()
        val emailText = email.text.toString().trim()
        val passwordText = password.text.toString().trim()

        if (nombreText.isBlank() || apellido1Text.isBlank() || emailText.isBlank() || passwordText.isBlank()) {
            Toast.makeText(this, "Completa todos los campos obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        val persona = hashMapOf(
            "id" to nuevoId,
            "nombre" to nombreText,
            "apellido1" to apellido1Text,
            "apellido2" to apellido2Text,
            "fecha_nacimiento" to fechaText,
            "email" to emailText,
            "password" to passwordText
        )

        val coleccionDestino = if (tipoSeleccionado == "Entrenador") "Entrenadores" else "Clientes"

        db.collection("GymElorrietaBD")
            .document("gym_01")
            .collection(coleccionDestino)
            .document(nuevoId)
            .set(persona)
            .addOnSuccessListener {
                Toast.makeText(this, "Registro exitoso ($nuevoId)", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainLogin::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al registrar: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
    }
    suspend fun obtenerSiguienteId(
        coleccionPadre: String,
        documentoGym: String,
        subcoleccion: String
    ): String {
        return try {
            val db = FirebaseFirestore.getInstance()

            // Obtenemos el último documento según su ID (descendente)
            val snapshot = db.collection(coleccionPadre)
                .document(documentoGym)
                .collection(subcoleccion)
                .orderBy(FieldPath.documentId(), Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .await()

            if (snapshot.isEmpty) {
                "C1" // si no hay documentos, empieza con C1
            } else {
                val ultimoId = snapshot.documents[0].id // ID del documento
                val soloLetras = ultimoId.replace(Regex("\\d+"), "")
                val soloNumeros = ultimoId.replace(Regex("\\D+"), "")

                val incrementar = soloNumeros.toInt() + 1
                "$soloLetras$incrementar"
            }

        } catch (e: Exception) {
            throw Exception("Error al obtener el siguiente ID: ${e.localizedMessage}")
        }
    }




