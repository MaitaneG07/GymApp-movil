package com.example.gymapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gymapp.adapter.WorkoutAdapter
import com.example.gymapp.model.gestores.FirebaseManager
import com.example.gymapp.model.entity.Entrenador
import com.example.gymapp.model.entity.Workout
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EntrenadorActivity : BaseActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: WorkoutAdapter
    private val workoutsList = mutableListOf<Workout>()
    private lateinit var db: FirebaseFirestore

    private var entrenador: Entrenador? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workouts)

        FirebaseApp.initializeApp(this)
        db = Firebase.firestore

        // Intent: intentar recibir entrenador
        entrenador = intent.getSerializableExtra("entrenador") as? Entrenador

        if (entrenador == null) {
            // Si no viene por Intent, cargar desde SharedPreferences + Firestore
            val sharedPref = getSharedPreferences("UserSession", MODE_PRIVATE)
            val id = sharedPref.getString("user_id", null)
            if (id != null) {
                db.collection("GymElorrietaBD")
                    .document("gym_01")
                    .collection("Entrenadores")
                    .document(id)
                    .get()
                    .addOnSuccessListener { doc ->
                        entrenador = doc.toObject(Entrenador::class.java)
                        if (entrenador == null) {
                            Toast.makeText(this, "No se pudo cargar el entrenador", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Log.d("EntrenadorActivity", "Entrenador cargado: ${entrenador?.nombre}")
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error al cargar el entrenador", Toast.LENGTH_SHORT).show()
                        finish()
                    }
            } else {
                Toast.makeText(this, "No se encontró el entrenador", Toast.LENGTH_SHORT).show()
                finish()
            }
        } else {
            Log.d("EntrenadorActivity", "Entrenador recibido por Intent: ${entrenador?.nombre}")
        }

        // Configurar menú de perfil
        val menuButton = findViewById<ImageButton>(R.id.imageViewPerfil)
        menuButton.setOnClickListener { view ->
            val popupMenu = PopupMenu(this, view)
            popupMenu.menuInflater.inflate(R.menu.perfil_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.menu_acceder_perfil -> {
                        accederPerfil()
                        true
                    }
                    R.id.menu_cerrar_sesion -> {
                        cerrarSesion()
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }

        // Configurar RecyclerView
        recyclerView = findViewById(R.id.recyclerViewWorkouts)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = WorkoutAdapter(
            workouts = workoutsList,
            lifecycleScope = lifecycleScope,
            onModificar = { workoutModificado -> modificarWorkout(workoutModificado) },
            onEliminar = { workout -> eliminarWorkout(workout.id) }
        )
        recyclerView.adapter = adapter

        cargarWorkoutsFirebase()

        // Configuración de agregar workout
        val editTextNombre: EditText = findViewById(R.id.etNuevoNombre)
        val editTextNivel: EditText = findViewById(R.id.etNuevoNivel)
        val editTextVideo: EditText = findViewById(R.id.etNuevoVideo)

        findViewById<Button>(R.id.buttonAñadir).setOnClickListener {
            val nombre = editTextNombre.text.toString().trim()
            val nivel = editTextNivel.text.toString().trim()
            val url = editTextVideo.text.toString().trim()

            if (nombre.isEmpty() || nivel.isEmpty() || url.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val btnAñadir = findViewById<Button>(R.id.buttonAñadir)
            btnAñadir.isEnabled = false

            lifecycleScope.launch {
                try {
                    val nuevoId = FirebaseManager.obtenerSiguienteIdWorkout()
                    val nuevoWorkout = Workout(
                        id = nuevoId,
                        nombre = nombre,
                        nivel = nivel,
                        video = url
                    )
                    FirebaseManager.agregarWorkoutConId(nuevoWorkout)

                    withContext(Dispatchers.Main) {
                        workoutsList.add(nuevoWorkout)
                        adapter.notifyItemInserted(workoutsList.size - 1)
                        editTextNombre.text.clear()
                        editTextNivel.text.clear()
                        editTextVideo.text.clear()
                        Toast.makeText(this@EntrenadorActivity, "Workout añadido: $nuevoId", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@EntrenadorActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                } finally {
                    withContext(Dispatchers.Main) {
                        btnAñadir.isEnabled = true
                    }
                }
            }
        }
    }

    private fun eliminarWorkout(workoutId: String) {
        lifecycleScope.launch {
            try {
                FirebaseManager.eliminarWorkoutPorId(workoutId)
                withContext(Dispatchers.Main) {
                    val index = workoutsList.indexOfFirst { it.id == workoutId }
                    if (index != -1) {
                        workoutsList.removeAt(index)
                        adapter.notifyItemRemoved(index)
                        Toast.makeText(this@EntrenadorActivity, "Eliminado", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@EntrenadorActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun modificarWorkout(workout: Workout) {
        lifecycleScope.launch {
            try {
                FirebaseManager.modificarWorkout(workout)
                withContext(Dispatchers.Main) {
                    val index = workoutsList.indexOfFirst { it.id == workout.id }
                    if (index != -1) {
                        workoutsList[index] = workout
                        adapter.notifyItemChanged(index)
                        Toast.makeText(this@EntrenadorActivity, "Workout modificado", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@EntrenadorActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun cerrarSesion() {
        val intent = Intent(this, MainLogin::class.java)
        startActivity(intent)
        finish()
    }

    private fun accederPerfil() {
        val intent = Intent(this, MainPerfilActivity::class.java)
        // Pasar el objeto completo del entrenador para evitar problemas
        intent.putExtra("entrenador", entrenador)
        startActivity(intent)
    }

    private fun cargarWorkoutsFirebase() {
        lifecycleScope.launch {
            try {
                workoutsList.clear()
                val workouts = FirebaseManager.obtenerWorkouts()
                workoutsList.addAll(workouts)
                adapter.notifyDataSetChanged()
            } catch (e: Exception) {
                Toast.makeText(this@EntrenadorActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
