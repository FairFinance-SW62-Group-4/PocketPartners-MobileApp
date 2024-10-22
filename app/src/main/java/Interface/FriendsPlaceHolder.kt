package Interface

import Beans.UsersInformation
import retrofit2.Call
import retrofit2.http.GET

interface FriendsPlaceHolder {
    @GET("api/v1/usersInformation")
    fun getListadoFriends(): Call<List<UsersInformation>>
}