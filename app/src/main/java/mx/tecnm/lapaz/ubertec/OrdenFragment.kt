package mx.tecnm.lapaz.ubertec

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [OrdenFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OrdenFragment : Fragment() {
    // TODO: Rename and change types of parameters
    lateinit var id : String
    lateinit var usuario : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            id = it.getInt("id").toString()
            usuario = it.getInt("usuario").toString()

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_orden, container, false)
        val btnOrdenar= v.findViewById<Button>(R.id.btnAgregar)
        val btnsumar = v.findViewById<Button>(R.id.btnSumar)
        val btnrestar = v.findViewById<Button>(R.id.btnRestar)
        val tvCantidad = v.findViewById<TextView>(R.id.tvContador)
        var contador = 0;
        val Nombre = v.findViewById<TextView>(R.id.tvNombreP)
        val PrecioP = v.findViewById<TextView>(R.id.tvPrecioD)
        val Detalles = v.findViewById<TextView>(R.id.tvDetalles)
        val Imagen = v.findViewById<ImageView>(R.id.ivImagenD)
        btnsumar.setOnClickListener {
            contador++
            tvCantidad.text = contador.toString()
        }
        btnrestar.setOnClickListener {
            if(contador > 0){
                contador--
                tvCantidad.text = contador.toString()
            }
        }
        btnOrdenar.setOnClickListener {
            CoroutineScope(Job()).launch {
                val platillo = Platillo.consultar(id)
                if (platillo != null) {
                    platillo.cantidad = contador.toString()
                    agregarPlatilloAlCarrito(platillo)
                    //Navigation.findNavController(v).navigate(R.id.action_ordenFragment_to_carrito)
                }
            }
            OrdentoCarrito(tvCantidad.text.toString())
            //Navigation.findNavController(v).navigate(R.id.action_ordenFragment_to_carrito)
        }
        CoroutineScope(Job()).launch {
            val platillo = Platillo.consultar(id)
            val bitmap = WebServiceREST.getImage(url+"/storage/${platillo?.id}.jpg")
            if (platillo != null) {
                if(bitmap != null){
                    activity?.runOnUiThread{
                        Nombre.text = platillo.nombre
                        PrecioP.text = platillo.precio.toString()
                        Detalles.text = platillo.descripcion
                        Imagen.setImageBitmap(bitmap)
                    }
                }

            }
            }
        return v
    }


    //METODO PARA MOSTRAR LAS RESEÑAS DEPENDIENDO EL ID DEL PLATILLO
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        CoroutineScope(Job()).launch {
            val platillo = Platillo.consultar(id)
            val strPlatillos = WebServiceREST.get(url + "/reseñas/${platillo?.id}")

            val gson = Gson()
            val jsonElement = gson.fromJson<JsonElement>(strPlatillos, JsonElement::class.java)

            val listReseñas: List<Reseña> = when {
                jsonElement.isJsonArray -> {
                    val jsonArray = jsonElement.asJsonArray
                    gson.fromJson(jsonArray, Array<Reseña>::class.java).toList()
                }
                jsonElement.isJsonObject -> {
                    // En este caso, strPlatillos es un objeto JSON, no un arreglo
                    // Puedes manejar el objeto de la manera que desees
                    // Por ejemplo, puedes crear una lista con un solo elemento
                    val reseña = gson.fromJson<Reseña>(jsonElement, Reseña::class.java)
                    listOf(reseña)
                }
                else -> {
                    // Manejar otros casos según tus necesidades
                    emptyList()
                }
            }

            requireActivity().runOnUiThread {
                val rvPlatillos = view.findViewById<RecyclerView>(R.id.rvResenaRV)
                val adaptador = ResenasAdapter(
                    listReseñas,
                    {
                    }, requireActivity() as MainActivity
                )
                rvPlatillos.adapter = adaptador
                val linearLayout = LinearLayoutManager(context)
                linearLayout.orientation = LinearLayoutManager.VERTICAL
                rvPlatillos.layoutManager = linearLayout
            }
        }
    }


    //METODO PARA AGREGAR AL CARRITO
    fun agregarPlatilloAlCarrito(platillo: Platillo) {
        val sharedPreferences =
            requireActivity().getSharedPreferences("carrito", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()

        // Obtener la lista actual de platillos del carrito desde SharedPreferences
        val platillosEnCarritoJson = sharedPreferences.getString("platillos", null)

        // Convertir la cadena JSON a una lista de Platillos usando Gson
        val gson = Gson()
        val platillosEnCarrito: MutableList<Platillo> = if (platillosEnCarritoJson != null) {
            gson.fromJson(
                platillosEnCarritoJson,
                object : TypeToken<MutableList<Platillo>>() {}.type
            )
        } else {
            mutableListOf()
        }

        // Agregar el nuevo platillo a la lista
        platillosEnCarrito.add(platillo)

        // Convertir la lista a una cadena JSON
        val nuevaListaJson = gson.toJson(platillosEnCarrito)

        // Guardar la nueva lista en SharedPreferences
        editor.putString("platillos", nuevaListaJson)
        editor.apply()

    }
    fun OrdentoCarrito(cantidad : String) {

            // Navegar al siguiente fragmento
            val c = Bundle()
            c.putString("cantidad", cantidad)
            getView()?.let {
                Navigation.findNavController(it)
                    .navigate(R.id.action_ordenFragment_to_carrito, c)
            }

        }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment OrdenFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            OrdenFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}