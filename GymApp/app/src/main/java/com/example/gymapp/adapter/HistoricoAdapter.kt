import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.gymapp.R
import com.example.gymapp.model.entity.Historico

class HistoricoAdapter(private val historicos: List<Historico>) :
    RecyclerView.Adapter<HistoricoAdapter.HistoricoViewHolder>() {

    class HistoricoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textNombre: TextView = itemView.findViewById(R.id.textViewNombre)
        val textNivel: TextView = itemView.findViewById(R.id.textViewNivel)
        val textTiempoTotal: TextView = itemView.findViewById(R.id.textViewTiempoTotal)
        val textTiempoPrevisto: TextView = itemView.findViewById(R.id.textViewTiempoPrevisto)
        val textFecha: TextView = itemView.findViewById(R.id.textViewFecha)
        val textPorcentaje: TextView = itemView.findViewById(R.id.textViewPorcentaje)
        val textVideo: TextView = itemView.findViewById(R.id.textViewVideo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoricoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_historico, parent, false)
        return HistoricoViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: HistoricoViewHolder, position: Int) {
        val historico = historicos[position]

        // Mostrar los datos en los TextViews
        holder.textNombre.text = historico.nombre
        holder.textNivel.text = historico.nivel
        holder.textTiempoTotal.text = historico.tiempoTotal
        holder.textTiempoPrevisto.text = historico.tiempoPrevisto
        holder.textFecha.text = historico.fechaInicio
        holder.textPorcentaje.text = historico.porcentaje as CharSequence?
        holder.textVideo.text = historico.video

        // Permitir abrir el video con una pulsación larga
        holder.textVideo.setOnLongClickListener {
            val url = historico.video?.trim()
            if (!url.isNullOrEmpty()) {
                try {
                    val finalUrl = if (url.startsWith("http")) url else "https://$url"
                    val intent = Intent(Intent.ACTION_VIEW, finalUrl.toUri())
                    holder.itemView.context.startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(holder.itemView.context, "Error al abrir el video", Toast.LENGTH_SHORT).show()
                    Log.e("HistoricoAdapter", "Error al abrir URL: ${e.message}")
                }
            } else {
                Toast.makeText(holder.itemView.context, "No hay URL válida", Toast.LENGTH_SHORT).show()
            }
            true
        }
    }

    override fun getItemCount(): Int = historicos.size
}
