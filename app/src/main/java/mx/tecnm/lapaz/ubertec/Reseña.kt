package mx.tecnm.lapaz.ubertec
import com.google.gson.Gson
import java.io.Serializable

    class Reseña(
    var id:Int,
    var nombre_usuario:String,
    var idPlatillo:Int,
    var descripcion:String
    ): Serializable {

        companion object {
            suspend fun consultar(id: String): Reseña? {
                val respuesta = WebServiceREST.get(url + "/reseñas/" + id)
                if (respuesta != "false") {
                    return Gson().fromJson<Reseña>(respuesta, Reseña::class.java)
                }
                return null
            }


            suspend fun agregarReseña(nombre_usuario: String,id_platilloF: Int,descripcion: String):Reseña?
            {
                val parametros = "nombre_usuario=${nombre_usuario}&id_platilloF=${id_platilloF}&descripcion=${descripcion}"
                val respuesta = WebServiceREST.post(url +"/Reseña/agregar", parametros)
                if (respuesta!="false") {
                    return Gson().fromJson<Reseña>(respuesta,Reseña::class.java)
                }
                return null
            }








        }
    }
