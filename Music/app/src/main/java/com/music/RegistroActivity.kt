package com.music

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
import androidx.lifecycle.lifecycleScope
import com.music.database.CancionesDatabase
import com.music.entity.Usuario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegistroActivity : AppCompatActivity() {

    private lateinit var db : CancionesDatabase
    private lateinit var user : EditText
    private lateinit var password : EditText


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainRegistro)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = CancionesDatabase(this)
        user = findViewById<EditText>(R.id.editTextUser)
        password = findViewById<EditText>(R.id.editTextPassword)

        findViewById<Button>(R.id.buttonRegistrado).setOnClickListener {

            val usuario = user.text.toString().trim()
            val contrasenya = password.text.toString().trim()

            if (usuario.isEmpty()) {
                Toast.makeText(this, "Ingresa un nombre de usuario", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (contrasenya.isEmpty() && contrasenya.length < 4) {
                Toast.makeText(this, "Ingresa una contraseña de al menos 4 caracteres", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val usuarioExistente = withContext(Dispatchers.IO) {
                        db.usuarioDao().getUsuarioByName(usuario)
                    }

                    if (usuarioExistente != null) {
                        Toast.makeText(
                            this@RegistroActivity,
                            "El usuario ya existe. Elige otro nombre",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@launch
                    }

                    val nuevoUsuario = Usuario(
                        name = usuario,
                        password = contrasenya,
                    )

                    withContext(Dispatchers.IO) {
                        db.usuarioDao().insertUsuario(nuevoUsuario)
                    }

                    Toast.makeText(
                        this@RegistroActivity,
                        "Registro exitoso. Ya puedes iniciar sesión",
                        Toast.LENGTH_LONG
                    ).show()

                    startActivity(Intent(this@RegistroActivity, MainActivity::class.java))
                    finish()
                } catch (e: Exception) {
                    Toast.makeText(
                        this@RegistroActivity,
                        "Error al registrar: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        findViewById<Button>(R.id.buttonVolver).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

    }
}