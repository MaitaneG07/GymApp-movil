package com.example.gymapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.TextView
import com.example.gymapp.model.Workout
import com.example.gymapp.R

class WorkoutAdapter (
    context: Context?,
    resource: Int,
    objects: List<Workout>?
): ArrayAdapter<Workout>(context!!, resource, objects!!){

    override fun getView (position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_workout, parent, false)

        val imageButton = view.findViewById(R.id.imageButton) as ImageButton
        val nombre = view.findViewById(R.id.textViewMostrarNombre) as TextView
        val nivel = view.findViewById(R.id.textViewMostrarNivel) as TextView
        val tiempoTotal = view.findViewById(R.id.textViewMostrarTiempoTotal) as TextView
        val tiempoPrevisto = view.findViewById(R.id.textViewMostrarTiempoPrevisto) as TextView
        val fecha = view.findViewById(R.id.textViewMostrarFecha) as TextView
        val porcentaje = view.findViewById(R.id.textViewMostrarPorcentaje) as TextView

        getItem(position)?.let {
            nombre.text = it.nombre
            nivel.text = it.nivel
            tiempoTotal.text = it.tiempoTotal
            tiempoPrevisto.text = it.tiempoPrevisto
            fecha.text = it.fecha
            porcentaje.text = it.completado
        }
        return view
    }
}