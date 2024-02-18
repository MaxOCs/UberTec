package mx.tecnm.lapaz.ubertec

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ResenasAdapter(
    var listaResenas: List<Reseña>,
    val onclick: (Reseña) -> Unit,
    val activity: Activity
) : RecyclerView.Adapter<ResenasAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView
        val tvDescripcion: TextView
        val ivFoto:ImageView


        init {
            tvNombre = view.findViewById(R.id.tvNombreR)
            tvDescripcion = view.findViewById(R.id.tvDescripcionR)
            ivFoto = view.findViewById(R.id.ivFotoR)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.renglon_resena, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        CoroutineScope(Job()).launch {
            // Puedes cargar una imagen asociada a la reseña si es necesario
            val bitmap = WebServiceREST.getImage(url+"/storage/R${listaResenas[position].id}.jpg")
            activity.runOnUiThread { viewHolder.ivFoto.setImageBitmap(bitmap) }
        }

        viewHolder.itemView.setOnClickListener {onclick(listaResenas[position]) }
        viewHolder.tvNombre.text = listaResenas[position].nombre_usuario
        viewHolder.tvDescripcion.text = listaResenas[position].descripcion
    }

    override fun getItemCount() = listaResenas.size
}