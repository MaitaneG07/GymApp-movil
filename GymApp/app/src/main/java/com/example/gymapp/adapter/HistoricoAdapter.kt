import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.gymapp.R
import com.example.gymapp.model.entity.Historico
import androidx.core.net.toUri

class HistoricoAdapter(private val historicos: List<Historico>) :
    RecyclerView.Adapter<HistoricoAdapter.HistoricoViewHolder>() {

    class HistoricoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val buttonPlay: ImageButton = view.findViewById(R.id.imageButton)
        val textNombre: TextView = view.findViewById(R.id.textViewMostrarNombre)
        val textNivel: TextView = view.findViewById(R.id.textViewMostrarNivel)
        val textTiempoTotal: TextView = view.findViewById(R.id.textViewMostrarTiempoTotal)
        val textTiempoPrevisto: TextView = view.findViewById(R.id.textViewMostrarTiempoPrevisto)
        val textFecha: TextView = view.findViewById(R.id.textViewMostrarFecha)
        val textPorcentaje: TextView = view.findViewById(R.id.textViewMostrarPorcentaje)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoricoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_historico, parent, false)
        return HistoricoViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: HistoricoViewHolder, position: Int) {
        val historico = historicos[position]

        // LOGS DE DEBUG - Agregar estos
        Log.d(TAG, "=== Item $position ===")
        Log.d(TAG, "Nombre: ${historico.nombre}")
        Log.d(TAG, "Fecha: ${historico.fechaInicio}")
        Log.d(TAG, "Nivel: ${historico.nivel}")
        Log.d(TAG, "Tiempo Total: ${historico.tiempoTotal}")
        Log.d(TAG, "Tiempo Previsto: ${historico.tiempoPrevisto}")
        Log.d(TAG, "Porcentaje: ${historico.porcentaje}")
        Log.d(TAG, "Video: ${historico.video}")

        holder.textNombre.text = historico.nombre
        holder.textFecha.text = historico.fechaInicio
        holder.textNivel.text = historico.nivel
        holder.textTiempoTotal.text = historico.tiempoTotal
        holder.textTiempoPrevisto.text = historico.tiempoPrevisto
        holder.textFecha.text = historico.fechaInicio
        holder.textPorcentaje.text = historico.porcentaje

//        val totalEjercicios = historicos.size
//        val completados = historicos.count { it.completado == true }
//        val porcentaje = if (totalEjercicios > 0) (completados * 100) / totalEjercicios else 0

        holder.buttonPlay.setOnClickListener {
            val direccionVideo = historico.video
            if (direccionVideo!!.isNotEmpty()) {
                try {
                    //busca todas las apps que puedan manejar ese tipo de URI (la url web)
                    // y abre la que el movil tenga  predeterminada
                    val intent = Intent(Intent.ACTION_VIEW, direccionVideo.toUri())
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

    override fun getItemCount(): Int = historicos.size
}