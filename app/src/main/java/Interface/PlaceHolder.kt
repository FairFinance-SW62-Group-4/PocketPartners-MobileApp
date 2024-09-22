package Interface

import Beans.FriendsList
import Beans.GroupRequest
import Beans.GroupResponse
import Beans.Grupo
import Beans.UsersInformation
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface PlaceHolder {
    @GET("groups")
    fun getListadoGroups(): Call<List<Grupo>>

    @GET("userFriendsList/userId/{userId}")
    fun getFriendsList(@Path("userId") userId: Int): Call<FriendsList>

    // Endpoint para obtener la información de todos los usuarios
    @GET("usersInformation/userId/{userId}")
    fun getUserInformation(@Path("userId") userId: Int): Call<UsersInformation>

    @POST("groups")
    fun createGroup(@Body groupRequest: GroupRequest): Call<GroupResponse>

    @POST("groups/{groupId}/members/{userId}")
    fun addMemberToGroup(@Path("groupId") groupId: Int, @Path("userId") userId: Int, @Body memberJson: Map<String, Any>): Call<Void>
}