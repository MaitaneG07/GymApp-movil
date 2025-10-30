package com.example.gymapp

import HistoricoAdapter
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.PopupMenu
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


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)

        db = FirebaseFirestore.getInstance()

        setContentView(R.layout.activity_historico)

        val cliente = intent.getSerializableExtra("cliente") as? Cliente

        if (cliente != null) {
            Log.d("WorkoutActivity", "Cliente recibido: ${cliente.nombre}, nivel: ${cliente.nivel}, id: ${cliente.id}")
            Toast.makeText(this, "Nivel recibido: ${cliente.nivel}", Toast.LENGTH_SHORT).show()
            findViewById<TextView>(R.id.mostrarLevel).text = cliente.nivel
        }

            findViewById<Button>(R.id.buttonPerfil).setOnClickListener {
                val intent = Intent(this, MainPerfilActivity::class.java)

        // En tu Activity o Fragment
        val menuButton = findViewById<ImageButton>(R.id.imageViewPerfil)
                // Si quieres pasar el cliente a la actividad de perfil:
                if (cliente != null) {
                    intent.putExtra("cliente", cliente)
                }
                startActivity(intent)
                finish()
            }

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

        recyclerView = findViewById(R.id.recyclerViewHistorico)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = HistoricoAdapter(historicoList)
        recyclerView.adapter = adapter

        cargarHistoricosFirebase(cliente!!.id)

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

    @SuppressLint("NotifyDataSetChanged")
    private fun cargarHistoricosFirebase(id: String) {

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
                Toast.makeText(this, "Error al cargar historicos: $exception", Toast.LENGTH_LONG).show()
            }
    }

}