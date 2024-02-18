package mx.tecnm.lapaz.ubertec
import com.google.gson.Gson
import java.io.Serializable

class Usuario (
    var id : Int,
    var nombre:String,
    var apellido:String,
    var correo :String,
    var numero :String,
    var password : String,
):Serializable {
    companion object{
        suspend fun iniciarSesion(correo:String,password: String): Usuario?
        {
            val parametros = "correo=${correo}&password=${password}"
            val respuesta = WebServiceREST.post(url+"/Usuarios/iniciarsesion",parametros)
            if (respuesta!="false") {
                return Gson().fromJson<Usuario>(respuesta,Usuario::class.java)
            }
            return null
        }
        suspend fun registrarUsuario(nombre: String,apellido: String,correo: String,numero: String,password: String):Usuario?
        {
            val parametros = "nombre=${nombre}&apellido=${apellido}&correo=${correo}&numero=${numero}&password=${password}"
            val respuesta = WebServiceREST.post(url +"/Usuarios/insertar", parametros)
            if (respuesta!="false") {
                return Gson().fromJson<Usuario>(respuesta,Usuario::class.java)
            }
            return null
        }
    }
}