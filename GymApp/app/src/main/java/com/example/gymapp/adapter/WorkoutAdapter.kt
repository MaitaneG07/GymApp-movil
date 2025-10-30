package com.example.gymapp.adapter

import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gymapp.R
import com.example.gymapp.model.gestores.FirebaseManager
import com.example.gymapp.model.entity.Workout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WorkoutAdapter(
    private val workouts: MutableList<Workout>, // Cambiado a MutableList
    private val lifecycleScope: LifecycleCoroutineScope,
    private val onModificar: (Workout) -> Unit,
    private val onEliminar: (Workout) -> Unit
) : RecyclerView.Adapter<WorkoutAdapter.ViewHolder>() {

    private var posicionEditando: Int = -1 // Solo 1 en edición

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val frontCard: CardView = itemView.findViewById(R.id.front_card)
        val backCard: CardView = itemView.findViewById(R.id.back_card)
        val tvNombre: EditText = itemView.findViewById(R.id.textViewNombre)
        val tvNivel: EditText = itemView.findViewById(R.id.textViewNivel)
        val tvVideo: EditText = itemView.findViewById(R.id.textViewVideo)
        val btnModificar: Button = itemView.findViewById(R.id.btnModificar)
        val btnEliminar: Button = itemView.findViewById(R.id.btnEliminar)
        val rvEjercicios: RecyclerView = itemView.findViewById(R.id.rv_ejercicios)
        var isFlipped = false
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_workouts, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val workout = workouts[position]
        val esModoEdicion = position == posicionEditando

        // === Datos ===
        holder.tvNombre.setText(workout.nombre)
        holder.tvNivel.setText(workout.nivel)
        holder.tvVideo.setText(workout.video)

        // === Modo Edición ===
        habilitarEdicion(holder, esModoEdicion)
        holder.btnModificar.text = if (esModoEdicion) holder.itemView.context.getString(R.string.icono_guardar) else holder.itemView.context.getString(R.string.icono_modificar)

        // === Botón Modificar / Guardar ===
        holder.btnModificar.setOnClickListener {
            if (esModoEdicion) {
                // === GUARDAR ===
                val nombre = holder.tvNombre.text.toString().trim()
                val nivel = holder.tvNivel.text.toString().trim()
                val video = holder.tvVideo.text.toString().trim()

                if (nombre.isEmpty() || nivel.isEmpty() || video.isEmpty()) {
                    Toast.makeText(holder.itemView.context, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val workoutModificado = workout.copy(
                    nombre = nombre,
                    nivel = nivel,
                    video = video
                )

                // ← Usa la función del Activity (sin tocar Firebase aquí)
                onModificar(workoutModificado)

                // Salir del modo edición
                posicionEditando = -1
                notifyItemChanged(position)
            } else {
                // === ENTRAR EN MODO EDICIÓN ===
                posicionEditando = position
                notifyDataSetChanged()
            }
        }

        // === Botón Eliminar ===
        holder.btnEliminar.setOnClickListener {
            onEliminar(workout)
        }

        // === Long click en video ===
        holder.tvVideo.setOnLongClickListener {
            val url = workout.video?.trim()
            if (!url.isNullOrEmpty()) {
                try {
                    val finalUrl = if (url.startsWith("http")) url else "https://$url"
                    val intent = Intent(Intent.ACTION_VIEW, finalUrl.toUri())
                    holder.itemView.context.startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(holder.itemView.context, "Error al abrir video", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(holder.itemView.context, "URL no válida", Toast.LENGTH_SHORT).show()
            }
            true
        }

        // === Flip Card ===
        holder.itemView.setOnClickListener {
            if (esModoEdicion) {
                Toast.makeText(holder.itemView.context, "Guarda o cancela edición primero", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!holder.isFlipped) {
                cargarEjercicios(holder, workout.id)
                flipToBack(holder)
            } else {
                flipToFront(holder)
            }
            holder.isFlipped = !holder.isFlipped
        }

        // === Reset visual ===
        holder.frontCard.visibility = View.VISIBLE
        holder.backCard.visibility = View.GONE
        holder.frontCard.rotationY = 0f
        holder.backCard.rotationY = 180f
        holder.isFlipped = false
    }

    private fun habilitarEdicion(holder: ViewHolder, habilitar: Boolean) {
        val context = holder.itemView.context
        val drawable = if (habilitar) android.R.drawable.edit_text else null

        listOf(holder.tvNombre, holder.tvNivel, holder.tvVideo).forEach { editText ->
            editText.apply {
                isFocusable = habilitar
                isFocusableInTouchMode = habilitar
                isClickable = habilitar
                drawable?.let { background = ContextCompat.getDrawable(context, it) }
            }
        }
    }

    private fun cargarEjercicios(holder: ViewHolder, workoutId: String?) {
        lifecycleScope.launch {
            try {
                val ejercicios = FirebaseManager.obtenerEjerciciosPorWorkout(workoutId) ?: emptyList()
                withContext(Dispatchers.Main) {
                    holder.rvEjercicios.layoutManager = LinearLayoutManager(holder.itemView.context)
                    holder.rvEjercicios.adapter = EjerciciosAdapter(ejercicios)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(holder.itemView.context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    holder.rvEjercicios.adapter = EjerciciosAdapter(emptyList())
                }
            }
        }
    }

    override fun getItemCount() = workouts.size

    // === Animaciones Flip ===
    private fun flipToBack(holder: ViewHolder) {
        val outAnim = ObjectAnimator.ofFloat(holder.frontCard, "rotationY", 0f, 90f)
        val inAnim = ObjectAnimator.ofFloat(holder.backCard, "rotationY", -90f, 0f)
        outAnim.duration = 300
        inAnim.duration = 300
        outAnim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: android.animation.Animator) {
                holder.frontCard.visibility = View.GONE
                holder.backCard.visibility = View.VISIBLE
                inAnim.start()
            }
        })
        outAnim.start()
    }

    private fun flipToFront(holder: ViewHolder) {
        val outAnim = ObjectAnimator.ofFloat(holder.backCard, "rotationY", 0f, 90f)
        val inAnim = ObjectAnimator.ofFloat(holder.frontCard, "rotationY", -90f, 0f)
        outAnim.duration = 300
        inAnim.duration = 300
        outAnim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: android.animation.Animator) {
                holder.backCard.visibility = View.GONE
                holder.frontCard.visibility = View.VISIBLE
                inAnim.start()
            }
        })
        outAnim.start()
    }
}