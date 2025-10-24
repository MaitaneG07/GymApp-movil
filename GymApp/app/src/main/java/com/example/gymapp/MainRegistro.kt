package com.example.gymapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gymapp.model.entity.Cliente
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
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

        //iniciamos firestore
        db = Firebase.firestore

        // Inicializar vistas
        nombre = findViewById(R.id.editTextNombre)
        apellido1 = findViewById(R.id.editTextApellido1)
        apellido2 = findViewById(R.id.editTextApellido2)
        fechaNacimiento = findViewById(R.id.editTextFecha)
        email = findViewById(R.id.InputemailRegistro)
        password = findViewById(R.id.InputPassword)
        btnRegistrar = findViewById(R.id.btnRegistrar)


        //evento del boton
        btnRegistrar.setOnClickListener {
            registrarUsuario()
        }
    }
    private fun registrarUsuario() {

        val nombreText = nombre.text.toString().trim()
        val apellido1Text = apellido1.text.toString().trim()
        val apellido2Text = apellido2.text.toString().trim()
        val fechaText = fechaNacimiento.text.toString().trim()
        val emailText = email.text.toString().trim()
        val passwordText = password.text.toString().trim()

        //Extrae los valores del formulario y elimina espacios innecesarios.
        //Verifica que los campos obligatorios no estén vacíos.
        if (nombreText.isBlank() || apellido1Text.isBlank() || emailText.isBlank() || passwordText.isBlank()) {
            Toast.makeText(this, "Completa todos los campos obligatorios", Toast.LENGTH_SHORT).show()
            return
        }
        //creamos el objeto
        val cliente = Cliente(
            id = UUID.randomUUID().toString(),
            nombre = nombreText,
            apellido1 = apellido1Text,
            apellido2 = apellido2Text,
            fecha_nacimiento = fechaText,
            email = emailText,
            password = passwordText
        )


        //guarda siguiendo el esqueleto de firestore
        db.collection("GymElorrietaBD")
            .document("gym_01")
            .collection("Clientes")
            .document(cliente.id)
            .set(cliente)

            //registro existoso
            .addOnSuccessListener {
                Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainLogin::class.java)
                startActivity(intent)
                finish() // Vuelve a la pantalla anterior
            }
            //registro fallido
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al registrar: ${e.message}", Toast.LENGTH_SHORT).show()
            }


    }
}

