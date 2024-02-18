package mx.tecnm.lapaz.ubertec

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.Navigation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Registro.newInstance] factory method to
 * create an instance of this fragment.
 */
class Registro : Fragment() {
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
        //declaracion de todas las variables del registro
        val frag = inflater.inflate(R.layout.fragment_registro, container, false)
        val btnRegistro = frag.findViewById<Button>(R.id.btnRegistrar)
        var Nombre = frag.findViewById<TextView>(R.id.etNombre)
        val apellido = frag.findViewById<EditText>(R.id.etApellido)
        val correo = frag.findViewById<EditText>(R.id.etEmail)
        val numero = frag.findViewById<EditText>(R.id.etNumero)
        var password = frag.findViewById<EditText>(R.id.etPassword)
        // val parametros = "nombre=${Nombre.text}&apellido=${apellido.text}&correo=${correo.text}&numero=${numero.text}&password=${password.text}"
        btnRegistro.setOnClickListener{
            CoroutineScope(Job()).launch {
                Usuario.registrarUsuario(Nombre.text.toString(), apellido.text.toString(),correo.text.toString(),numero.text.toString(),password.text.toString())
                //mostrar el mensaje en el hilo principal
                activity?.runOnUiThread {
                    Toast.makeText(activity, "¡Registro exitoso!", Toast.LENGTH_SHORT).show()
                    Navigation.findNavController(frag).navigate(R.id.action_registro_to_loginFragment)
                }
            }
            //Navigation.findNavController(frag).navigate(R.id.action_registro_to_loginFragment)
        }
        return frag
    }
    

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Registro.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Registro().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}