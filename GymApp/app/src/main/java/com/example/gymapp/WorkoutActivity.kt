package com.example.gymapp

import WorkoutAdapter
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gymapp.Firebase.Workout
import com.example.gymapp.model.entity.Cliente
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore

@Suppress("DEPRECATION")
class WorkoutActivity : BaseActivity() {

    //estos dos son para la prueba:
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: WorkoutAdapter

    private val workoutList = mutableListOf<Workout>()
    private lateinit var db: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)

        db = FirebaseFirestore.getInstance()

        setContentView(R.layout.activity_workout)


        val botonEntrenador : Button = findViewById(R.id.buttonEntrenador)
        val botonVolver : Button = findViewById(R.id.buttonVolverWO)
        val cliente = intent.getSerializableExtra("cliente") as? Cliente

        if (cliente != null) {
            Log.d("WorkoutActivity", "Cliente recibido: ${cliente.nombre}, nivel: ${cliente.nivel}")
            Toast.makeText(this, "Nivel recibido: ${cliente.nivel}", Toast.LENGTH_SHORT).show()
            findViewById<TextView>(R.id.mostrarLevel).text = cliente.nivel
        }

        findViewById<Button>(R.id.buttonPerfil).setOnClickListener {
            val intent = Intent(this, MainPerfilActivity::class.java)

            finish()

        }

        botonEntrenador.setOnClickListener {
            val intent = Intent(this, EntrenadorActivity::class.java)
            startActivity(intent)
        }

        botonVolver.setOnClickListener {
            val intent = Intent(this, MainLogin::class.java)
            startActivity(intent)
            finish()
        }

        recyclerView = findViewById(R.id.recyclerViewWorkout)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = WorkoutAdapter(workoutList)
        recyclerView.adapter = adapter



        cargarWorkoutsFirebase()


    }




    @SuppressLint("NotifyDataSetChanged")
    private fun cargarWorkoutsFirebase() {

        workoutList.clear()

        db.collection("GymElorrietaBD")
            .document("gym_01")
            .collection("Workouts")
            .get()
            .addOnSuccessListener { documents ->
                for (doc in documents) {
                    Log.d("Firestore", "Documento id: ${doc.id} - datos: ${doc.data}")
                    val workout = doc.toObject(Workout::class.java)
                    workout.id = doc.id

                    workoutList.add(workout)
                }
                Log.d("WorkoutActivity", "Workouts cargados: ${workoutList.size}")
                Toast.makeText(this, "Workouts cargados: ${workoutList.size}", Toast.LENGTH_SHORT).show()
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al cargar workouts: $exception", Toast.LENGTH_LONG).show()
            }
    }

}