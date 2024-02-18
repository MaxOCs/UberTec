package mx.tecnm.lapaz.ubertec

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Carrito.newInstance] factory method to
 * create an instance of this fragment.
 */
class Carrito : Fragment() {
    // TODO: Rename and change types of parameters
    lateinit var cantidad : String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            cantidad = it.getString("cantidad").toString()
        }
    }

    private lateinit var adaptador: PlatillosAdapter_carrito
    private lateinit var tvTotalCarrito: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        CoroutineScope(Job()).launch {
            tvTotalCarrito = view.findViewById<TextView>(R.id.tvTotal)
            val listaPlatillos =  obtenerPlatillosDelCarrito()
            var totalcarrito = calcularTotalPrecio(listaPlatillos)
            adaptador = PlatillosAdapter_carrito(listaPlatillos,
                {
                    eliminarPlatillo(it)
                }, requireActivity() as MainActivity
            )
            requireActivity().runOnUiThread {
                //PRIMER ADAPTADOR(PLATILLOS)
                val rvPlatillos = view.findViewById<RecyclerView>(R.id.rvCarrito)
                rvPlatillos.adapter = adaptador
                val linearLayout = LinearLayoutManager(context)
                linearLayout.orientation = LinearLayoutManager.VERTICAL
                rvPlatillos.layoutManager = linearLayout
                tvTotalCarrito.text = totalcarrito.toString()
                // Notificar al adaptador que los datos han cambiado
                adaptador.notifyDataSetChanged()
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val listaDePlatillos: List<Platillo> = obtenerPlatillosDelCarrito()
        var usuario = obtenerUsuarioDesdeSharedPreferences()
        val vista = inflater.inflate(R.layout.fragment_carrito, container, false)
        val tvTotal = vista.findViewById<TextView>(R.id.tvTotal)
        val btncomprar = vista.findViewById<Button>(R.id.btnComprar)
        btncomprar.setOnClickListener {
            CoroutineScope(Job()).launch {
                if (usuario != null && tvTotal != null) {
                    val total = tvTotal.text.toString()
                    // Insertar el pedido
                    val pedidoId = withContext(Dispatchers.IO) {
                        Pedido.insert(usuario.id, total)
                    }
                    // Verificar si la inserción del pedido fue exitosa
                    var ultimopedido = Pedido.ultimoPedido()
                    if (ultimopedido != null) {
                        Pedido.estado_pedido(ultimopedido,"1")
                    }
                    if ( ultimopedido != null) {
                        // Recorrer la lista de platillos e insertar cada platillo en la base de datos
                        for (platillo in listaDePlatillos) {
                            val platilloId = platillo.id.toString()
                            // Insertar el platillo en el pedido
                            withContext(Dispatchers.IO) {
                                Pedido.platillo_pedido(ultimopedido, platilloId.toInt(), platillo.cantidad)
                            }
                        }
                        // Navegar al siguiente fragmento
                        activity?.runOnUiThread {
                            Navigation.findNavController(vista).navigate(R.id.action_carrito_to_comprados)
                        }
                    }
                }
            }
            borrarCarrito()
        }
        return vista
    }

    private fun actualizarTotal(listaPlatillos: List<Platillo>) {
        // Calcula el nuevo total y actualiza el TextView
        val nuevoTotal = calcularTotalPrecio(listaPlatillos)
        tvTotalCarrito.text = nuevoTotal.toString()
    }

    fun obtenerPlatillosDelCarrito(): List<Platillo> {
        val sharedPreferences = requireActivity().getSharedPreferences("carrito", Context.MODE_PRIVATE)
        val platillosEnCarritoJson = sharedPreferences.getString("platillos", null)

        // Convertir la cadena JSON a una lista de Platillos usando Gson
        val gson = Gson()
        return if (platillosEnCarritoJson != null) {
            gson.fromJson(platillosEnCarritoJson, object : TypeToken<List<Platillo>>() {}.type)
        } else {
            emptyList()
        }
    }

    //optener el total
    fun calcularTotalPrecio(platillos: List<Platillo>): Double {
        var totalPrecio = 0.0

        for (platillo in platillos) {
            totalPrecio += platillo.precio
        }

        return totalPrecio
    }
    //obtener el usuaro
    fun obtenerUsuarioDesdeSharedPreferences(): Usuario? {
        val sharedPreferences = requireActivity().getSharedPreferences("usuario", Context.MODE_PRIVATE)
        val usuarioJson = sharedPreferences.getString("usuario", null)

        if (usuarioJson != null) {
            // Deserializar la cadena JSON a un objeto Usuario
            val gson = Gson()
            return gson.fromJson(usuarioJson, Usuario::class.java)
        }

        return null
    }
    //eliminar el platillo seleccionado
    fun eliminarPlatillo(platillo: Platillo) {
        val listaPlatillos = obtenerPlatillosDelCarrito().toMutableList()
        // Imprimir la lista antes de eliminar
        Log.d("platilloeliminado", "platillo a eliminar: ${platillo}")
        Log.d("MiAplicacion", "Lista antes de eliminar: ${listaPlatillos}")
        listaPlatillos.remove(platillo)
        // Imprimir la lista después de eliminar
        Log.d("MiAplicacion", "Lista después de eliminar: ${listaPlatillos}")
        guardarPlatillosEnCarrito(listaPlatillos)
        // Actualizar el adaptador directamente
        adaptador.actualizarLista(listaPlatillos)

        actualizarTotal(listaPlatillos)
    }
    fun guardarPlatillosEnCarrito(platillos: List<Platillo>) {
        val sharedPreferences = requireActivity().getSharedPreferences("carrito", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val platillosJson = Gson().toJson(platillos)
        editor.putString("platillos", platillosJson)
        editor.apply()
    }
    fun borrarCarrito(){
        val sharedplatillos = requireActivity().getSharedPreferences("carrito", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedplatillos.edit()
        editor.clear()
        editor.apply()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Carrito.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Carrito().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}