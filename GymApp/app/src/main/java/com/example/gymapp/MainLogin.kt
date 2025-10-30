package com.example.gymapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
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
    private fun iniciarSesion() {

        //Obtiene el texto introducido por el usuario en los campos de email y contraseña.
        // Usa .trim() para eliminar espacios en blanco al principio y al final.
        val email = usuario.text.toString().trim()
        val password = password.text.toString().trim()


        //Verifica si alguno de los campos está vacío o contiene solo espacios.
        //Si es así, muestra un mensaje de advertencia y detiene la ejecución de la función.
        if (email.isBlank() || password.isBlank()) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }


        //Accede a la base de datos Firestore.
        //Navega por la estructura: colección "GymEloiteBD" → documento "gym_01" → subcolección "Clientes"
        db.collection("GymElorrietaBD") // Asegúrate de que el nombre sea correcto
            .document("gym_01")
            .collection("Clientes")

            //Realiza una consulta filtrada: busca documentos donde el campo email coincida con el valor introducido, y el campo password también.
            //Ejecuta la consulta con .get() para obtener los resultados.
            .whereEqualTo("email", email)
            .whereEqualTo("password", password)
            .get()
            .addOnSuccessListener { documents ->
                //Toma el primer documento encontrado (si existe) y lo convierte
                // en un objeto de tipo Cliente usando el mapeo automático de Firestore.
                val cliente = documents.documents.firstOrNull()?.toObject(Cliente::class.java)

                //Accede a la base de datos Firestore.
                //Navega por la estructura: colección "GymEloiteBD" → documento "gym_01" → subcolección "Entrenadores" y devuelve un objeto
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
                                val sharedPref = getSharedPreferences("UserSession", MODE_PRIVATE)
                                sharedPref.edit {
                                    putString("user_email", email)
                                    putString("user_password", password)
                                    putString("user_role", "cliente")
                                }
                                //Crea un Intent para abrir la pantalla WorkoutActivity.
                                //Le pasa el objeto cliente como extra para que esté disponible en la siguiente actividad.
                                //Llama a finish() para cerrar la pantalla actual y evitar que el usuario vuelva atrás con el botón de retroceso.
                                Toast.makeText(
                                    this,
                                    "Bienvenido ${cliente.nombre}",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                startActivity(Intent(this, HistoricoActivity::class.java).apply {
                                    // enviar el objeto cliente/id a la siguiente actividad, y lo voy a etiquetar con la clave "cliente".”
                                    putExtra("cliente", cliente)
                                })
                                finish()
                            }

                                entrenador != null -> {
                                    val sharedPref = getSharedPreferences("UserSession", MODE_PRIVATE)
                                    sharedPref.edit {
                                        putString("user_email", email)
                                        putString("user_password", password)
                                        putString("user_role", "entrenador")
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
}


