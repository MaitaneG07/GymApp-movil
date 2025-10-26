import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.gymapp.Firebase.Workout
import com.example.gymapp.R

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
        holder.textFecha.text = workout.fechaInicio

        // En estos pongo N/A porque de momento no se como sacarlos
        holder.textNivel.text = workout.nivel
        holder.textTiempoTotal.text = "N/A"
        holder.textTiempoPrevisto.text = "N/A"

        val totalEjercicios = workout.ejercicios.size
        val completados = workout.ejercicios.count { it.completado }
        val porcentaje = if (totalEjercicios > 0) (completados * 100) / totalEjercicios else 0
        holder.textPorcentaje.text = "$porcentaje%"

        holder.buttonPlay.setOnClickListener {
            val direccionVideo = workout.video
            if (direccionVideo.isNotEmpty()) {
                try {
                    //busca todas las apps que puedan manejar ese tipo de URI (la url web)
                    // y abre la que el movil tenga  predeterminada
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(direccionVideo))
                    intent.addCategory(Intent.CATEGORY_BROWSABLE)
                    holder.itemView.context.startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(holder.itemView.context, "No es posible reproducir el video", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(holder.itemView.context, "El Workout no tiene ningún video", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount(): Int = workouts.size
}