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
import com.example.gymapp.model.entity.Historico
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore

@Suppress("DEPRECATION")
class HistoricoActivity : BaseActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HistoricoAdapter
    private val historicoList = mutableListOf<Historico>()
    private lateinit var db: FirebaseFirestore

    private var cliente: Cliente? = null // Guardamos el cliente si viene por intent

    @SuppressLint("MissingInflatedId", "SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)
        db = FirebaseFirestore.getInstance()

        setContentView(R.layout.activity_historico)

        // Intent: intentar recibir cliente
        cliente = intent.getSerializableExtra("cliente") as? Cliente

        // Si viene cliente por intent, mostramos su nivel
        cliente?.let {
            Log.d("HistoricoActivity", "Cliente recibido: ${it.nombre}, nivel: ${it.nivel}, id: ${it.id}")
            findViewById<TextView>(R.id.mostrarLevel).text = it.nivel
        }

        // Configuración RecyclerView
        recyclerView = findViewById(R.id.recyclerViewHistorico)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = HistoricoAdapter(historicoList)
        recyclerView.adapter = adapter

        // Botón de menú de perfil
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

        // Cargar históricos: intent o SharedPreferences
        val userId = cliente?.id ?: getSharedPreferences("UserSession", MODE_PRIVATE)
            .getString("user_id", null)

        if (userId != null) {
            cargarHistoricosFirebase(userId)
        } else {
            Toast.makeText(this, "No se pudo obtener el id del usuario", Toast.LENGTH_SHORT).show()
            Log.e("HistoricoActivity", "No hay cliente ni id en SharedPreferences")
        }
    }

    private fun cerrarSesion() {
        val intent = Intent(this, MainLogin::class.java)
        startActivity(intent)
        finish()
    }

    private fun accederPerfil() {
        val intent = Intent(this, MainPerfilActivity::class.java)
        // Pasamos el cliente si lo tenemos
        cliente?.let { intent.putExtra("cliente", it) }
        startActivity(intent)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun cargarHistoricosFirebase(id: String) {
        historicoList.clear()

        db.collection("GymElorrietaBD")
            .document("gym_01")
            .collection("Clientes")
            .document(id)
            .collection("Historico")
            .get()
            .addOnSuccessListener { documents ->
                for (doc in documents) {
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
