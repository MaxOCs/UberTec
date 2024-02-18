package mx.tecnm.lapaz.ubertec
import com.google.gson.Gson
import java.io.Serializable

class Platillo(
    var id:Int,
    var nombre:String,
    var descripcion:String,
    var precio:Double,
    var cantidad:String,

): Serializable {
    companion object{
        suspend fun consultar(id:String): Platillo?
        {
            val respuesta = WebServiceREST.get(url+"/platillos/"+id)
            if (respuesta!="false") {
                return Gson().fromJson<Platillo>(respuesta,Platillo::class.java)
            }
            return null
        }

    }
    override fun toString(): String {
        return "Platillo(id=$id, nombre=$nombre, descripcion=$descripcion, precio=$precio)"
    }
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Platillo) return false

        if (id != other.id) return false
        if (nombre != other.nombre) return false
        // Puedes agregar más comparaciones según sea necesario

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + nombre.hashCode()
        // Puedes agregar más propiedades al cálculo del hashCode según sea necesario
        return result
    }
}