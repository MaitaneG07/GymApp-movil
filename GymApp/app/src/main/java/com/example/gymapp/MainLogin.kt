package com.example.gymapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gymapp.model.entity.Cliente
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class MainLogin : BaseActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var Usuario: EditText
    private lateinit var Password: EditText

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
        Usuario = findViewById(R.id.InputEmail)
        Password = findViewById(R.id.InputContrasenya)


        findViewById<Button>(R.id.btnRegistrate).setOnClickListener {
            val intent = Intent(this, MainRegistro::class.java)
            startActivity(intent)
            finish()
        }

        findViewById<Button>(R.id.btnLogin).setOnClickListener {
            iniciarSesion()

        }

    }

    private fun iniciarSesion() {

        val email = Usuario.text.toString().trim()
        val password = Password.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("GymElorrietaBD")
            .document("gym_01")
            .collection("Clientes")// Usa "Clientes" según tu Firebase
            .whereEqualTo("email", email)
            .whereEqualTo("password", password)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val cliente = documents.documents[0].toObject(Cliente::class.java)

                    Toast.makeText(this, "Bienvenido ${cliente?.nombre}", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, WorkoutActivity::class.java)
                    intent.putExtra("cliente", cliente)  // Pasar el objeto completo
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}

