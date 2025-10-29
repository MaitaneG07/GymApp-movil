package com.example.gymapp

import HistoricoAdapter
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gymapp.adapter.WorkoutAdapter
import com.example.gymapp.model.entity.Entrenador
import com.example.gymapp.model.entity.Historico
import com.example.gymapp.model.entity.Workout
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore

class EntrenadorActivity : BaseActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: WorkoutAdapter

    private val workoutsList = mutableListOf<Workout>()
    private lateinit var db: FirebaseFirestore
    private lateinit var editTextUrl: EditText

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)
        db = FirebaseFirestore.getInstance()
        setContentView(R.layout.activity_workouts)

        val entrenador = intent.getSerializableExtra("entrenador") as? Entrenador

        if (entrenador != null) {
            Log.d("WorkoutActivity", "Cliente recibido: ${entrenador.nombre}, id: ${entrenador.id}")
        }

        val menuButton = findViewById<ImageButton>(R.id.imageViewPerfil)

        menuButton.setOnClickListener { view ->
            // Crear el PopupMenu
            val popupMenu = PopupMenu(this, view)
            popupMenu.menuInflater.inflate(R.menu.perfil_menu, popupMenu.menu)

            // Manejar los clicks en las opciones
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.menu_acceder_perfil -> {
                        // Código para acceder al perfil
                        accederPerfil()
                        true
                    }
                    R.id.menu_cerrar_sesion -> {
                        // Código para cerrar sesión
                        cerrarSesion()
                        true
                    }
                    else -> false
                }
            }
            // Mostrar el menú
            popupMenu.show()
        }

        editTextUrl = findViewById(R.id.textViewVideo)

        //Para poder acceder a la URL
        editTextUrl.setOnLongClickListener {
            val url = editTextUrl.text.toString().trim()

            if (url.isNotEmpty()) {
                val finalUrl = if (url.startsWith("http://") || url.startsWith("https://")) {
                    url
                } else {
                    "http://$url"
                }

                val intent = Intent(Intent.ACTION_VIEW, finalUrl.toUri())
                startActivity(intent)
            } else {
                Toast.makeText(this, "Introduce una URL válida", Toast.LENGTH_SHORT).show()
            }

            true
        }

        recyclerView = findViewById(R.id.recyclerViewWorkouts)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = WorkoutAdapter(workoutsList)
        recyclerView.adapter = adapter

        cargarWorkoutsFirebase(entrenador!!.id)

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
        workoutsList.clear()

        db.collection("GymElorrietaBD")
            .document("gym_01")
            .collection("Workouts")
            .get()
            .addOnSuccessListener { documents ->
                for (doc in documents) {
                    Log.d("Firestore", "Documento id: ${doc.id} - datos: ${doc.data}")
                    val workout = doc.toObject(Workout::class.java)
                    workout.id = doc.id

                    workoutsList.add(workout)
                }
                Log.d("WorkoutsActivity", "Workouts cargados: ${workoutsList.size}")
                Toast.makeText(this, "Workouts cargados: ${workoutsList.size}", Toast.LENGTH_SHORT).show()
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al cargar workouts: $exception", Toast.LENGTH_LONG).show()
            }
    }
}