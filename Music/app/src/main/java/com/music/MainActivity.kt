package com.music

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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var db : CancionesDatabase
    private lateinit var user : EditText
    private lateinit var password : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = CancionesDatabase(this)
        user = findViewById(R.id.editTextUser)
        password = findViewById(R.id.editTextPassword)

        findViewById<Button>(R.id.buttonAcceder).setOnClickListener {
            val username = user.text.toString().trim()
            val contrasenya: String = password.text.toString().trim()

            if (username.isEmpty() || contrasenya.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val usuario = withContext(Dispatchers.IO) {
                        db.usuarioDao().login(username, contrasenya)
                    }

                    if (usuario != null) {
                        Toast.makeText(
                            this@MainActivity,
                            "Bienvenido ${usuario.name}",
                            Toast.LENGTH_SHORT
                        ).show()

                        val intent = Intent(this@MainActivity, PerfilMain::class.java)
                        intent.putExtra("usuarioId", usuario.usuarioId)
                        intent.putExtra("name", usuario.name)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            "Usuario o contraseña incorrectos",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(
                        this@MainActivity,
                        "Error: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        findViewById<Button>(R.id.buttonRegistro).setOnClickListener {
            startActivity(Intent(this, RegistroActivity::class.java))
            finish()
        }
    }
}