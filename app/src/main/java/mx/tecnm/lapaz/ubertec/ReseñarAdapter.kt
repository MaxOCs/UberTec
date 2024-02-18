package mx.tecnm.lapaz.ubertec

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ReseñarAdapter(var listaPlatillos:List<Platillo>,
                     val Onclick:(Platillo)-> Unit, val activity : Activity) :
    RecyclerView.Adapter<ReseñarAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivFoto:ImageView
        val tvNombre: TextView
        val tvDescripcion:TextView
        val tvPrecio: TextView
        val btnRes: Button
        init {
            ivFoto = view.findViewById(R.id.ivFotoRE)
            tvNombre = view.findViewById(R.id.tvNombreRE)
            tvDescripcion = view.findViewById(R.id.tvDescripcionRE)
            tvPrecio = view.findViewById(R.id.tvPrecioRE)
            btnRes = view.findViewById(R.id.btnReseñar)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.renglonrr, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        CoroutineScope(Job()).launch {
            val bitmap = WebServiceREST.getImage("${url}/storage/${listaPlatillos[position].id}.jpg")

            //para jalar una imagen desede el websevice
            activity.runOnUiThread{
                viewHolder.ivFoto.setImageBitmap(bitmap)
            }
        }
        viewHolder.ivFoto.setOnClickListener{Onclick(listaPlatillos[position])}
        viewHolder.btnRes.setOnClickListener{Onclick(listaPlatillos[position])}
        viewHolder.tvNombre.text = listaPlatillos[position].nombre
        viewHolder.tvDescripcion.text = listaPlatillos[position].descripcion
        viewHolder.tvPrecio.text = listaPlatillos[position].precio.toString()
        viewHolder.itemView.setOnClickListener {
         Onclick(listaPlatillos[position])
        }
    }



    override fun getItemCount() = listaPlatillos.size
}
