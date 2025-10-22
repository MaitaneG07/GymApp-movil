package com.example.gymapp

import WorkoutAdapter
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gymapp.model.Workout

class WorkoutActivity : BaseActivity() {

    //estos dos son para la prueba:
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: WorkoutAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout)

        val botonEntrenador : Button = findViewById(R.id.buttonEntrenador)
        val botonVolver : Button = findViewById(R.id.buttonVolverWO)



        findViewById<Button>(R.id.buttonPerfil).setOnClickListener {
            val intent = Intent(this, MainPerfilActivity::class.java)
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

        //codigo para probar el adapter
        recyclerView = findViewById(R.id.recyclerViewWorkout)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val workoutsPrueba = listOf(
            Workout("Piernas día 1", "Principiante", "45 min", "60 min", "2023-10-01", "100%"),
            Workout("Espalda + Bíceps", "Intermedio", "40 min", "50 min", "2023-10-10", "80%"),
            Workout("HIIT fullbody", "Avanzado", "30 min", "30 min", "2023-10-18", "90%")
        )

        adapter = WorkoutAdapter(workoutsPrueba)
        recyclerView.adapter = adapter
    }
}