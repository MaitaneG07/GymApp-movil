package com.example.gymapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gymapp.R
import com.example.gymapp.model.entity.Cliente
import com.example.gymapp.model.entity.Entrenador

class PerfilAdapter<T : Any>(private val usuario: T) :
    RecyclerView.Adapter<PerfilAdapter<T>.UsuarioViewHolder>() {

    inner class UsuarioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre: TextView = itemView.findViewById(R.id.editTextNombre)
        val tvApellido1: TextView = itemView.findViewById(R.id.editTextApellido1)
        val tvApellido2: TextView = itemView.findViewById(R.id.editTextApellido2)
        val tvFecha: TextView = itemView.findViewById(R.id.editTextFechaNacimiento)
        val tvEmail: TextView = itemView.findViewById(R.id.editTextEmail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuarioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_perfil, parent, false)
        return UsuarioViewHolder(view)
    }

    override fun onBindViewHolder(holder: UsuarioViewHolder, position: Int) {
        when (usuario) {
            is Cliente -> {
                holder.tvNombre.text = usuario.nombre
                holder.tvApellido1.text = usuario.apellido1
                holder.tvApellido2.text = usuario.apellido2
                holder.tvFecha.text = usuario.fechaNacimiento
                holder.tvEmail.text = usuario.email
            }
            is Entrenador -> {
                holder.tvNombre.text = usuario.nombre
                holder.tvApellido1.text = usuario.apellido1
                holder.tvApellido2.text = usuario.apellido2
                holder.tvFecha.text = usuario.fechaNacimiento
                holder.tvEmail.text = usuario.email
            }
        }
    }

    override fun getItemCount(): Int = 1
}
