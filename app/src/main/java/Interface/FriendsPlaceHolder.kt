package Interface

import Beans.UsersInformation
import retrofit2.Call
import retrofit2.http.GET

interface FriendsPlaceHolder {
    @GET("usersInformation")
    fun getListadoFriends(): Call<List<UsersInformation>>
}