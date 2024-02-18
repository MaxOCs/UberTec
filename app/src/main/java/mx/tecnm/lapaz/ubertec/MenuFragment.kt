package mx.tecnm.lapaz.ubertec

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.app.NotificationCompat
import androidx.navigation.Navigation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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
 * Use the [MenuFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MenuFragment : Fragment() {
    // TODO: Rename and change types of parameters
    lateinit var usuario : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            usuario = it.getString("usuario").toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_menu, container, false)
        //val button = v.findViewById<Button>(R.id.btnPlatillos)
        val btncarrito = v.findViewById<Button>(R.id.btnCarrito)
        val btnPedidos= v.findViewById<Button>(R.id.btnPedidos)
        val tvbotanas=v.findViewById<TextView>(R.id.tvBotana)
        val tvBebidas = v.findViewById<TextView>(R.id.tvBebida)
        val tvComida = v.findViewById<TextView>(R.id.tvComida)
        val tvDesayuno = v.findViewById<TextView>(R.id.tvDesayuno)
        //val tvSesion= v.findViewById<TextView>(R.id.tvSesion)
        //tvSesion.text = usuario
        var Categoria = "0"

        btncarrito.setOnClickListener {
            Navigation.findNavController(v).navigate(R.id.action_menuFragment_to_carrito)
        }
        btnPedidos.setOnClickListener {
            Navigation.findNavController(v).navigate(R.id.action_menuFragment_to_comprados)
        }
        tvComida.setOnClickListener { Categoria = "2"
        cambarAlFractoPlatillos(Categoria)
        }
        tvDesayuno.setOnClickListener{Categoria="1"
        cambarAlFractoPlatillos(Categoria)
        }
        tvBebidas.setOnClickListener{
            Categoria ="4"
            cambarAlFractoPlatillos(Categoria)
        }
        tvbotanas.setOnClickListener{
            Categoria = "3"
            cambarAlFractoPlatillos(Categoria)
        }
            verificarNotificacionesPendientes()
            return v
    }
    //cambiar y mandar el id por parametro
    fun cambarAlFractoPlatillos(categories: String) {
        val c = Bundle()
        c.putString("categoria", categories)
        getView()?.let {
            Navigation.findNavController(it)
                .navigate(R.id.action_menuFragment_to_platillosFragment, c)
        }

    }
    public fun verificarNotificacionesPendientes() {


        GlobalScope.launch(Dispatchers.IO) {
            val hayNotificaciones = obtenerEstadoNotificaciones()


            Log.e("MiAplicacion", "Hay notificaciones: $hayNotificaciones")

            if (hayNotificaciones) {
                // Aquí puedes mostrar una notificación al usuario
                launch(Dispatchers.Main) {
                    mostrarNotificacion()
                }
            }
        }
    }

    private suspend fun obtenerEstadoNotificaciones(): Boolean {
        try {
            val pedido = WebServiceREST.get(url + "/Ver/idPedidoUser/"+usuario)
            val urlString = "http://192.168.1.99/Restaurant/public/Notificar/obtener/$pedido"

            //val urlString = "http://192.168.74.53/Restaurant/public/verificar_notificaciones.php"
            val url = URL(urlString)



            // Abrir la conexión
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"

            val responseCode = connection.responseCode

            if (responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = reader.readLine()

                Log.d("MiAplicacion", "Respuesta del servidor: $response")

                // Asegúrate de que la respuesta sea "1" para notificaciones pendientes
                return response == "1"
            } else {
                Log.d("MiAplicacion", "Error en la respuesta del servidor. Código: $responseCode")
                return false
            }
        } catch (e: Exception) {
            Log.e("MiAplicacion", "Error al obtener estado de notificaciones", e)
            return false
        }
    }

    private fun mostrarNotificacion() {

        val notificationManager = activity?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "mi_canal_notificaciones"

        val notificationBuilder = NotificationCompat.Builder(
            requireContext(),
            channelId
        )
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Nueva notificación")
            .setContentText("Puedes pasar por tu pedido")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Canal de notificación", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val notificationId = System.currentTimeMillis().toInt()
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MenuFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MenuFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}

