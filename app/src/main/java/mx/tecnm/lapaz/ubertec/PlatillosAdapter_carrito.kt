package mx.tecnm.lapaz.ubertec
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PlatillosAdapter_carrito(var listaPlatillos:List<Platillo>,
                               val Onclick:(Platillo)-> Unit, val activity : Activity) :
    RecyclerView.Adapter<PlatillosAdapter_carrito.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivFoto:ImageView
        val tvNombre: TextView
        val tvPrecio: TextView
        val tvCantidad: TextView
        val btnEliminar:ImageButton
        init {
            ivFoto = view.findViewById(R.id.ivFotoC)
            tvCantidad = view.findViewById(R.id.tvCantidadP)
            tvNombre = view.findViewById(R.id.tvNombrePlatillocarrito)
            tvPrecio = view.findViewById(R.id.tvPrecioC)
            btnEliminar=view.findViewById(R.id.btnEliminar)

        }


    }

    fun actualizarLista(nuevaLista: List<Platillo>) {
        listaPlatillos = nuevaLista
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.renglon_carrito, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        CoroutineScope(Job()).launch {
            val bitmap = WebServiceREST.getImage(url+"/storage/${listaPlatillos[position].id}.jpg")
            //para jalar una imagen desede el websevice
            activity.runOnUiThread{
                viewHolder.ivFoto.setImageBitmap(bitmap)
            }
        }
        //listaPlatillos[position].cantidad = cantidad
        viewHolder.tvCantidad.text = listaPlatillos[position].cantidad
        viewHolder.btnEliminar.setOnClickListener{Onclick(listaPlatillos[position])}
        //viewHolder.ivFoto.setOnClickListener{Onclick(listaPlatillos[position])}
        viewHolder.tvNombre.text = listaPlatillos[position].nombre
        viewHolder.tvPrecio.text = listaPlatillos[position].precio.toString()
        //viewHolder.itemView.setOnClickListener {
          //  Onclick(listaPlatillos[position])
        //}
    }

    override fun getItemCount() = listaPlatillos.size
}
