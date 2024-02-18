package mx.tecnm.lapaz.ubertec

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PlatillosFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PlatillosFragment : Fragment() {
    // TODO: Rename and change types of parameters
    lateinit var categoria : String
    lateinit var usuario : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            categoria = it.getString("categoria").toString()
            usuario = it.getString("usuario").toString()

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
            return inflater.inflate(R.layout.fragment_platillos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        CoroutineScope(Job()).launch {
            val strPlatillos =
                WebServiceREST.get(url + "/platillos/categoria/"+categoria)
            val listaPlatillos = Gson().fromJson<Array<Platillo>>(
                        strPlatillos, Array<Platillo>::class.java).toList()
            val adaptador = PlatillosAdapter(listaPlatillos,
                {
                   cambarAlFractoLogin(it)
                }, requireActivity() as MainActivity
            )
            requireActivity().runOnUiThread {
                val rvPlatillos = view.findViewById<RecyclerView>(R.id.rvPlatillos)
                rvPlatillos.adapter = adaptador
                val linearLayout = LinearLayoutManager(context)
                linearLayout.orientation = LinearLayoutManager.VERTICAL
                rvPlatillos.layoutManager = linearLayout
            } 
        }
    }
    //cambiar y mandar el id por parametro
    fun cambarAlFractoLogin(platillo: Platillo) {
        //Toast.makeText(applicationContext,"si jalo padre"+ platillo.nombre, Toast.LENGTH_SHORT).show()
        val c = Bundle()
        c.putInt("id", platillo.id)
        c.putString("usuario", usuario)
        getView()?.let {
            findNavController(it).navigate(
                R.id.action_platillosFragment_to_ordenFragment,
                c
            )
        }


    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PlatillosFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PlatillosFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}