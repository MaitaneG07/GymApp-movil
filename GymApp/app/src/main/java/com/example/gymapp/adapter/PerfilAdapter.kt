package com.example.gymapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gymapp.R
import com.example.gymapp.model.entity.Cliente

class PerfilAdapter(private val clientes: List<Cliente>) :
    RecyclerView.Adapter<PerfilAdapter.ClienteViewHolder>() {

    class ClienteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewNombre: TextView = itemView.findViewById(R.id.editTextNombre)

        val textViewApellido1: TextView = itemView.findViewById(R.id.editTextApellido1)

        val textViewApellido2: TextView = itemView.findViewById(R.id.editTextApellido2)

        val textViewFecha: TextView = itemView.findViewById(R.id.editTextFechaNacimiento)
        val textViewEmail: TextView = itemView.findViewById(R.id.editTextEmail)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClienteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_perfil, parent, false)
        return ClienteViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClienteViewHolder, position: Int) {
        val cliente = clientes[position]

        holder.textViewNombre.text = cliente.nombre
        holder.textViewApellido1.text=cliente.apellido1
        holder.textViewApellido2.text=cliente.apellido2
        holder.textViewFecha.text = cliente.fecha_nacimiento
        holder.textViewEmail.text = cliente.email

    }

    override fun getItemCount(): Int = clientes.size
}