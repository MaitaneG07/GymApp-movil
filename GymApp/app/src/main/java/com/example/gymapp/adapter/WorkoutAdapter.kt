import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gymapp.R
import com.example.gymapp.model.Workout

class WorkoutAdapter(private val workouts: List<Workout>) :
    RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder>() {

    class WorkoutViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val buttonPlay: ImageButton = view.findViewById(R.id.imageButton)
        val textNombre: TextView = view.findViewById(R.id.textViewMostrarNombre)
        val textNivel: TextView = view.findViewById(R.id.textViewMostrarNivel)
        val textTiempoTotal: TextView = view.findViewById(R.id.textViewMostrarTiempoTotal)
        val textTiempoPrevisto: TextView = view.findViewById(R.id.textViewMostrarTiempoPrevisto)
        val textFecha: TextView = view.findViewById(R.id.textViewMostrarFecha)
        val textPorcentaje: TextView = view.findViewById(R.id.textViewMostrarPorcentaje)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_workout, parent, false)
        return WorkoutViewHolder(view)
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        val workout = workouts[position]
        holder.textNombre.text = workout.nombre
        holder.textNivel.text = workout.nivel
        holder.textTiempoTotal.text = workout.tiempoTotal
        holder.textTiempoPrevisto.text = workout.tiempoPrevisto
        holder.textFecha.text = workout.fecha
        holder.textPorcentaje.text = workout.porcentajeCompletado

        // Acción para botón (opcional)
        holder.buttonPlay.setOnClickListener {
            // Acción para "play" (puedes mostrar un Toast, por ejemplo)
        }
    }

    override fun getItemCount(): Int = workouts.size
}