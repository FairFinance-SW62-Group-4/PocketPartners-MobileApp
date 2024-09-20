package Interface

import Beans.Grupo
import retrofit2.Call
import retrofit2.http.GET

interface PlaceHolder {
    @GET("groups")
    fun getListadoGroups(): Call<List<Grupo>>
}