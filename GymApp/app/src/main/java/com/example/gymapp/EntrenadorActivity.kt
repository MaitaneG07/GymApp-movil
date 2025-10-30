package com.example.gymapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.Spinner
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gymapp.adapter.WorkoutAdapter
import com.example.gymapp.model.gestores.FirebaseManager
import com.example.gymapp.model.entity.Entrenador
import com.example.gymapp.model.entity.Workout
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EntrenadorActivity : BaseActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: WorkoutAdapter

    private var workoutSeleccionado: Workout? = null

    private val workoutsList = mutableListOf<Workout>()

    private lateinit var spinner: Spinner

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workouts)

        FirebaseApp.initializeApp(this)

        val entrenador = intent.getSerializableExtra("entrenador") as? Entrenador

        if (entrenador == null) {
            Toast.makeText(this, "Error al cargar los datos del entrenador", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        Log.d("WorkoutActivity", "Entrenador recibido: ${entrenador.nombre}, id: ${entrenador.id}")

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

        // Crear adapter con lifecycleScope
        adapter = WorkoutAdapter(
            workouts = workoutsList,
            lifecycleScope = lifecycleScope,
            onModificar = { workoutModificado -> modificarWorkout(workoutModificado) },
            onEliminar = { workout -> eliminarWorkout(workout.id) }
        )
        recyclerView.adapter = adapter

        // Cargar workouts
        cargarWorkoutsFirebase(entrenador.id)


        val editTextNombre: EditText = findViewById(R.id.etNuevoNombre)
        val editTextNivel: EditText = findViewById(R.id.etNuevoNivel)
        val editTextVideo: EditText = findViewById(R.id.etNuevoVideo)



        //Cargar spinner
        spinner = findViewById(R.id.spinnerNivel)

        val niveles = listOf("Todos", "Principiante", "Intermedio", "Avanzado")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, niveles)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = spinnerAdapter

        // 🔹 Listener del spinner
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val nivelSeleccionado = niveles[position]
                filtrarWorkouts(nivelSeleccionado)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }


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
                    // Obtener el siguiente ID personalizado
                    val nuevoId = FirebaseManager.obtenerSiguienteIdWorkout()

                    // Crear el Workout con ID personalizado
                    val nuevoWorkout = Workout(
                        id = nuevoId,
                        nombre = nombre,
                        nivel = nivel,
                        video = url
                    )

                    // Guardar en Firebase usando el ID personalizado
                    FirebaseManager.agregarWorkoutConId(nuevoWorkout)

                    // Actualizar UI
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
    private fun filtrarWorkouts(nivel: String) {
        val filtrados = if (nivel == "Todos") {
            allWorkoutsList
        } else {
            allWorkoutsList.filter { it.nivel.equals(nivel, ignoreCase = true) }
        }

        workoutsList.clear()
        workoutsList.addAll(filtrados)
        adapter.notifyDataSetChanged()
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
        startActivity(intent)
        finish()
    }

    private fun cargarWorkoutsFirebase(id: String) {
        lifecycleScope.launch {
            try {
                workoutsList.clear()
                val workouts = FirebaseManager.obtenerWorkouts()
                workoutsList.addAll(workouts)

                // 🔹 Guarda una copia completa para el filtro
                allWorkoutsList.clear()
                allWorkoutsList.addAll(workouts)

                adapter.notifyDataSetChanged()
            } catch (e: Exception) {
                Toast.makeText(
                    this@EntrenadorActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}

