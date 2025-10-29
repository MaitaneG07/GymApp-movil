package com.example.gymapp

import HistoricoAdapter
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gymapp.model.entity.Cliente
import com.example.gymapp.model.entity.Workout
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore

@Suppress("DEPRECATION")
class HistoricoActivity : BaseActivity() {

    //estos dos son para la prueba:
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HistoricoAdapter

    private val workoutList = mutableListOf<Workout>()
    private lateinit var db: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)

        db = FirebaseFirestore.getInstance()

        setContentView(R.layout.activity_historico)


        val botonEntrenador : Button = findViewById(R.id.buttonEntrenador)
        val botonVolver : Button = findViewById(R.id.buttonVolverWO)
        val cliente = intent.getSerializableExtra("cliente") as? Cliente

        if (cliente != null) {
            Log.d("WorkoutActivity", "Cliente recibido: ${cliente.nombre}, nivel: ${cliente.nivel}, id: ${cliente.id}")
            Toast.makeText(this, "Nivel recibido: ${cliente.nivel}", Toast.LENGTH_SHORT).show()
            findViewById<TextView>(R.id.mostrarLevel).text = cliente.nivel
        }

            findViewById<Button>(R.id.buttonPerfil).setOnClickListener {
                val intent = Intent(this, MainPerfilActivity::class.java)

                // Si quieres pasar el cliente a la actividad de perfil:
                if (cliente != null) {
                    intent.putExtra("cliente", cliente)
                }
                startActivity(intent)
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

        adapter = HistoricoAdapter(historicoList)
        recyclerView.adapter = adapter

        cargarWorkoutsFirebase(cliente!!.id)

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun cargarWorkoutsFirebase(id: String) {

        historicoList.clear()


    @SuppressLint("NotifyDataSetChanged")
    private fun cargarWorkoutsFirebase() {

        workoutList.clear()

        db.collection("GymElorrietaBD")
            .document("gym_01")
            .collection("Clientes")
            .document(id)
            .collection("Historico")
            .get()
            .addOnSuccessListener { documents ->
                for (doc in documents) {
                    Log.d("Firestore", "Documento id: ${doc.id} - datos: ${doc.data}")
                    val historico = doc.toObject(Historico::class.java)
                    historico.id = doc.id

                    historicoList.add(historico)
                }
                Log.d("HistoricoActivity", "Historicos cargados: ${historicoList.size}")
                Toast.makeText(this, "Historicos cargados: ${historicoList.size}", Toast.LENGTH_SHORT).show()
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al cargar workouts: $exception", Toast.LENGTH_LONG).show()
            }
    }

}