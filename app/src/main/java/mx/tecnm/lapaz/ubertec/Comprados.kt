package mx.tecnm.lapaz.ubertec

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
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
 * Use the [Comprados.newInstance] factory method to
 * create an instance of this fragment.
 */
class Comprados : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val vista = inflater.inflate(R.layout.fragment_comprados, container, false)
        val tvNombre = vista.findViewById<TextView>(R.id.tvNombrepedido)
        val tvApellido = vista.findViewById<TextView>(R.id.tvApellido)
        val usuario = obtenerUsuarioDesdeSharedPreferences()

        if (usuario != null) {
            tvNombre.text = usuario.nombre
            tvApellido.text =usuario.apellido
        }
        return vista;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        CoroutineScope(Job()).launch {
            val usuario = obtenerUsuarioDesdeSharedPreferences()
            val strPlatillos =
                WebServiceREST.get(url + "/ticket/platillos/${usuario?.id}/1")
            val listaPlatillos =  Gson().fromJson<Array<Platillo>>(
                strPlatillos, Array<Platillo>::class.java).toList()
            val adaptador = ReseñarAdapter(listaPlatillos,
                {
                    cambarAlFractoLogin(it)

                }, requireActivity() as MainActivity
            )
            requireActivity().runOnUiThread {
                val rvPlatillos = view.findViewById<RecyclerView>(R.id.rvComprados)
                rvPlatillos.adapter = adaptador
                val linearLayout = LinearLayoutManager(context)
                linearLayout.orientation = LinearLayoutManager.VERTICAL
                rvPlatillos.layoutManager = linearLayout
            }
        }
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

    fun cambarAlFractoLogin(platillo: Platillo)
    {
        //Toast.makeText(applicationContext,"si jalo padre"+ platillo.nombre, Toast.LENGTH_SHORT).show()
        val c = Bundle()
        c.putInt("id", platillo.id)
        getView()?.let { Navigation.findNavController(it)
            .navigate(R.id.action_comprados_to_pantallaresena, c) }
    }

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


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Comprados.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Comprados().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}