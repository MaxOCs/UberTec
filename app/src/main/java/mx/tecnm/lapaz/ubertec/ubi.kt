package mx.tecnm.lapaz.ubertec
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ubi.newInstance] factory method to
 * create an instance of this fragment.
 */
class ubi : Fragment(R.layout.fragment_ubi) {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var locationManager: LocationManager
    private val MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 5
    private val latitudMacro: Double = 24.119891
    private val longitudMacro: Double = -110.310000
    private val radioTierra: Double = 6378.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_ubi, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                // Puedes mostrar un diálogo explicando por qué necesitas permisos
            } else {
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
                )
            }
        } else {
            usarGPS()
        }

    }



    private fun usarGPS() {
        locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        val location = if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        } else {
            null
        }
        if (location != null) {
            onLocationChanged(location)
        }
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            3000L,
            10f,
            LocationListener { onLocationChanged(it) }
        )
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    usarGPS()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "No se han dado permisos para usar el GPS",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
    private fun onLocationChanged(location: Location) {
        if (location != null) {
            view?.findViewById<TextView>(R.id.tvDistancia)?.text =
                "Distancia " + distancia(location.latitude, location.longitude).toString()+" metros"
        }
    }
    private fun distancia(latitud: Double, longitud: Double): Double {
        val ilat = (latitudMacro - latitud) * Math.PI / 180
        val ilong = (longitudMacro - longitud) * Math.PI / 180
        val a = Math.pow(Math.sin(ilat / 2), 2.0) +
                Math.cos(latitud * Math.PI / 180) *
                Math.cos(latitudMacro * Math.PI / 180) *
                Math.pow(Math.sin(ilong / 2), 2.0)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return radioTierra * c
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ubi.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ubi().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}