// com.example.gymapp.adapter.EjerciciosAdapter.kt
package com.example.gymapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gymapp.R
import com.example.gymapp.model.entity.Ejercicio

class EjerciciosAdapter(
    private val ejercicios: List<Ejercicio>
) : RecyclerView.Adapter<EjerciciosAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre: TextView = itemView.findViewById(R.id.tv_nombre)
        val tvSeries: TextView = itemView.findViewById(R.id.tv_series)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_entrenador, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ejercicio = ejercicios[position]

        holder.tvNombre.text = ejercicio.nombre

        // Mostrar el número de series
        val numSeries = ejercicio.series?.size ?: 0
        holder.tvSeries.text = if (numSeries > 0) {
            "${numSeries}x"
        } else {
            "0x"
        }
    }

    override fun getItemCount() = ejercicios.size
}