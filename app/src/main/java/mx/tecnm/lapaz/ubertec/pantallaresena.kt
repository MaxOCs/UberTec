package mx.tecnm.lapaz.ubertec

import android.Manifest
import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.Navigation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [pantallaresena.newInstance] factory method to
 * create an instance of this fragment.
 */
class pantallaresena : Fragment() {
    // TODO: Rename and change types of parameters

    lateinit var usuario: String
    lateinit var idPlatillo: String
    val REQUEST_IMAGE_CAPTURE = 124
    val REQUEST_TAKE_PHOTO = 123
    val MY_PERMISSIONS_REQUEST_CAMERA = 100
    val CAMERA_PREF = "camera_pref"
    val ALLOW_KEY = "ALLOWED"
    lateinit var currentPhotoPath: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

            usuario = it.getString("usuario").toString()
            idPlatillo = it.getInt("id").toString()

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val reseñaPantalla = inflater.inflate(R.layout.fragment_pantallaresena, container, false)
        val btnGenerarReseña = reseñaPantalla.findViewById<Button>(R.id.btnDarReseña)
        val btnFoto = reseñaPantalla.findViewById<Button>(R.id.btnDarReseña2)
        val textoR = reseñaPantalla.findViewById<TextView>(R.id.etReseña)

        val recuperar = obtenerUsuarioDesdeSharedPreferences()
        btnGenerarReseña.setOnClickListener {
            CoroutineScope(Job()).launch {

                if (recuperar != null) {
                    Reseña.agregarReseña(
                        recuperar.nombre,
                        idPlatillo.toInt(),
                        textoR.text.toString()
                    )
                    activity?.runOnUiThread {
                        Toast.makeText(activity, "¡Reseña agregada!", Toast.LENGTH_SHORT).show()
                    }
                } else {

                }
            }
        }

        btnFoto.setOnClickListener {
            CoroutineScope(Job()).launch {


                val activity = requireActivity()
                if (activity is MainActivity) {
                    val idR = WebServiceREST.get(url + "/Reseña/ultimoid")
                    activity.dispatchTakePictureIntent("R$idR")
                } else {
                    Log.e("E", "q pasa")
                }
            }


        }

        return reseñaPantalla

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


    fun obtenerUsuarioDesdeSharedPreferences(): Usuario? {
        val sharedPreferences = requireActivity().getSharedPreferences("usuario", Context.MODE_PRIVATE)
        val usuarioJson = sharedPreferences.getString("usuario", null)

        if (usuarioJson != null) {
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
         * @return A new instance of fragment pantallaresena.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            pantallaresena().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}