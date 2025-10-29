package com.example.gymapp.adapter

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.gymapp.R
import com.example.gymapp.model.entity.Historico
import com.example.gymapp.model.entity.Workout

class WorkoutAdapter(private val workouts: List<Workout>) :
    RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder>() {

    class WorkoutViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textNombre: EditText = view.findViewById(R.id.textViewNombre)
        val textNivel: EditText = view.findViewById(R.id.textViewNivel)
        val textVideo: EditText = view.findViewById(R.id.textViewVideo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_workouts, parent, false)
        return WorkoutViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        val workout = workouts[position]

        // LOGS DE DEBUG - Agregar estos
        Log.d(TAG, "=== Item $position ===")
        Log.d(TAG, "Nombre: ${workout.nombre}")
        Log.d(TAG, "Nivel: ${workout.nivel}")
        Log.d(TAG, "Video: ${workout.video}")

        holder.textNombre.text = workout.nombre as Editable?
        holder.textNivel.text = workout.nivel as Editable?
        holder.textVideo.text = workout.video as Editable?

//        val totalEjercicios = historicos.size
//        val completados = historicos.count { it.completado == true }
//        val porcentaje = if (totalEjercicios > 0) (completados * 100) / totalEjercicios else 0

    }

    override fun getItemCount(): Int = workouts.size
}