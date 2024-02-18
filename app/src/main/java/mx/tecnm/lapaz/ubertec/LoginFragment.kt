package mx.tecnm.lapaz.ubertec

import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation.findNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuInflater
import android.widget.PopupMenu
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {
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
    @SuppressLint("SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val direc = inflater.inflate(R.layout.fragment_login, container, false)
        val correo = direc.findViewById<EditText>(R.id.etUsuario)
        val password = direc.findViewById<EditText>(R.id.etpPassword)
        val registro = direc.findViewById<TextView>(R.id.tvRegistro)

        //Inicio de sesion
        registro.setOnClickListener {
            //WebServiceREST.get(url +"Usuario/iniciarsesion/${correo.text.toString()}/${password.text.toString()}")
            findNavController(direc).navigate(R.id.action_loginFragment_to_registro)
        }

        val irgps = direc.findViewById<Button>(R.id.botonRedondo)
        val acerca = direc.findViewById<TextView>(R.id.tvGPS)
        //Para ir al gps
        registerForContextMenu(irgps)
        irgps.setOnClickListener{
            val popup = PopupMenu(requireContext(), irgps)
            popup.menuInflater.inflate(R.menu.configuracion, popup.menu)
            popup.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menuUbucacion -> { findNavController(direc).navigate(R.id.action_loginFragment_to_ubi)}
                    R.id.menuAcercaDe -> { acerca.text = "Diseñado por maximiliano ortiz y Marco Josue"}
                    //R.id.menuSistema -> { vista.text = "POP "+ resources.getText(R.string.menuSistema).toString()}
                    //R.id.menuImagen -> { vista.text = "POP "+  resources.getText(R.string.menuImagen).toString()}
                    //R.id.menuTema -> { vista.text = "POP "+ resources.getText(R.string.menuTema).toString()}
                    //R.id.menuSearch -> { vista.text = "POP SEARCH"}
                }
                true
            }
            popup.show()
            true
        }


        //para  registrarte
        val button = direc.findViewById<Button>(R.id.btnIniciarsesion)
        button.setOnClickListener {
                CoroutineScope(Job()).launch {
                    var correoText = correo?.text.toString()
                    var passwordText = password?.text.toString()
                val usuario = Usuario.iniciarSesion(correoText,passwordText)
                    activity?.runOnUiThread{
                if(usuario!= null){
                    iniciarmenu(usuario)
                        //findNavController(direc).navigate(R.id.action_loginFragment_to_menuFragment)
                    //findNavController(direc).navigate(R.id.action_loginFragment_to_menuFragment)
                }else{
                    Toast.makeText(activity, "El correo o contraseña no existe", Toast.LENGTH_SHORT).show()
                } } }
        }
        return direc
    }


    fun iniciarmenu(usuario: Usuario) {
        val sharedPreferences = requireActivity().getSharedPreferences("usuario", Context.MODE_PRIVATE)
        val sharedplatillos = requireActivity().getSharedPreferences("carrito", Context.MODE_PRIVATE)
        // Obtener el correo del usuario actualmente guardado en SharedPreferences
        val usuarioG = obtenerUsuarioDesdeSharedPreferences()
        val correoGuardado = obtenerUsuarioDesdeSharedPreferences()
        if (correoGuardado != null && correoGuardado.correo == usuario.correo) {
            // El correo del usuario del parámetro coincide con el usuario guardado en SharedPreferences
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            // Convertir el objeto Usuario a una cadena JSON
            val gson = Gson()
            val usuarioJson = gson.toJson(usuario)

            // Guardar la cadena JSON en SharedPreferences
            editor.putString("usuario", usuarioJson)
            editor.apply()

            // Navegar al siguiente fragmento
            val c = Bundle()
            c.putString("usuario", usuario.id.toString())
            getView()?.let { findNavController(it).navigate(R.id.action_loginFragment_to_menuFragment, c) }


        } else {
            // El correo del usuario del parámetro no coincide con el usuario guardado en SharedPreferences pero lo guardamos aun asi
            val editor2: SharedPreferences.Editor = sharedPreferences.edit()
            // Convertir el objeto Usuario a una cadena JSON
            val gson = Gson()
            val usuarioJson = gson.toJson(usuario)
            editor2.putString("usuario", usuarioJson)
            editor2.apply()

            // Borra las preferencias
            val editor: SharedPreferences.Editor = sharedplatillos.edit()
            editor.clear()
            editor.apply()
            //pasar al sig fragment
            val c = Bundle()
            c.putString("usuario", usuario.correo.toString())
            getView()?.let { findNavController(it).navigate(R.id.action_loginFragment_to_menuFragment, c) }
            //Toast.makeText(requireContext(), "No se puede iniciar sesión. Verifica el correo.", Toast.LENGTH_SHORT).show()
        }
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

    /*fun iniciarmenu(usuario: Usuario) {
        val sharedPreferences = requireActivity().getSharedPreferences("usuario", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        val correoGuardado = sharedPreferences.getString("usuario", null)
        // Convertir el objeto Usuario a una cadena JSON
        val gson = Gson()
        val usuarioJson = gson.toJson(usuario)
        // Guardar la cadena JSON en SharedPreferences
        editor.putString("usuario", usuarioJson)
        editor.apply()
        // Navegar al siguiente fragmento
        val c = Bundle()
        c.putString("usuario", usuario.correo.toString())
        getView()?.let { findNavController(it).navigate(R.id.action_loginFragment_to_menuFragment, c) }
    }*/



    //PARA LAS NOTIFICACIONES


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LoginFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoginFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


}