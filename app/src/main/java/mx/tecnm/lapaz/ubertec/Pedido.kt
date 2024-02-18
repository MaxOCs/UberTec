package mx.tecnm.lapaz.ubertec

import com.google.gson.Gson
import java.io.Serializable

class Pedido (
    var id: Int,
    var id_usuario:Int,
    var total:String
): Serializable {
    companion object{
        suspend fun insert (id_usuario: Int , total: String): Pedido?
        {
            val parametros = "id_usuario=${id_usuario}&total=${total}"
            val respuesta = WebServiceREST.post(url+"/pedido/insertar", parametros)
            if (respuesta!="false") {
                return Gson().fromJson<Pedido>(respuesta,Usuario::class.java)
            }
            return null
        }
        suspend fun platillo_pedido (id_pedido:Int, id_platillo: Int, cantidad:String): Pedido?
        {
            val parametros = "id_pedido=${id_pedido}&id_platillo=${id_platillo}&cantidad=${cantidad}"
            val respuesta = WebServiceREST.post(url+"/pedido/pedidoPlatillo", parametros)
            if (respuesta!="false") {
                return Gson().fromJson<Pedido>(respuesta,Pedido::class.java)
            }
            return null
        }

        suspend fun ultimoPedido(): Int? {
            val respuesta = WebServiceREST.get(url + "/pedido/obtenerUltimoPedido")
            if (respuesta != "false") {
                return Gson().fromJson<Int>(respuesta, Int::class.java)
            }
            return null
        }
        suspend fun estado_pedido (id_pedido:Int, estado:String): Pedido?
        {
            val parametros = "id_pedido=${id_pedido}&estado=${estado}"
            val respuesta = WebServiceREST.post(url+"/pedido/pedidoEstado", parametros)
            if (respuesta!="false") {
                return Gson().fromJson<Pedido>(respuesta,Pedido::class.java)
            }
            return null
        }
    }
}