package Interface

import Beans.AuthenticatedUserResource
import Beans.Expense
import Beans.FriendListRequest
import Beans.FriendsList
import Beans.GroupJoin
import Beans.GroupRequest
import Beans.GroupResponse
import Beans.Grupo
import Beans.Payment
import Beans.SignInRequest
import Beans.SignUpRequest
import Beans.User
import Beans.UserInformationRequest
import Beans.UsersInformation
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface PlaceHolder {
    //GROUPS
    @GET("api/v1/groups")
    fun getListadoGroups(): Call<List<Grupo>>

    @POST("api/v1/groups")
    fun createGroup(
        @Header("Authorization") authHeader: String,
        @Body groupRequest: GroupRequest
    ): Call<GroupResponse>

    @GET("api/v1/groups/{groupId}")
    fun getGruposPorUserId(
        @Header("Authorization") authHeader: String,
        @Path("groupId") groupId: Int
    ): Call<Grupo>

    @POST("api/v1/groups/{groupId}/members/{userId}")
    fun addMemberToGroup(
        @Header("Authorization") authHeader: String,
        @Path("groupId") groupId: Int,
        @Path("userId") userId: Int
    ): Call<GroupJoin>

    @GET("api/v1/groups/members/{userId}")
    fun getGruposUnidosPorUserId(
        @Header("Authorization") authHeader: String,
        @Path("userId") userId: Int
    ): Call<List<GroupJoin>>

    //USERS

    @GET("api/v1/usersInformation/userId/{userId}")
    fun getUserInformation(
        @Header("Authorization") authHeader: String,
        @Path("userId") userId: Int
    ): Call<UsersInformation>

    @GET("api/v1/usersInformation")
    fun getAllUsersInformation(
        @Header("Authorization") authHeader: String,
    ): Call<List<UsersInformation>>

    @POST("api/v1/usersInformation")
    fun createUserInformation(
        @Header("Authorization") authHeader: String,
        @Body userInformationRequest: UserInformationRequest
    ): Call<UsersInformation>

    //USERS FRIEND LISTS

    @GET("api/v1/userFriendsList/userId/{userId}")
    fun getFriends(
        @Header("Authorization") authHeader: String,
        @Path("userId") userId: Int
    ): Call<FriendsOfUser>
    
    @GET("api/v1/userFriendsList/userId/{userId}")
    fun getUserFriendsListById(
        @Header("Authorization") authHeader: String,
        @Path("userId") userId: Int
    ): Call<FriendsList>

    @POST("api/v1/userFriendsList/addFriend")
    fun addUserToFriendsList(
        @Header("Authorization") authHeader: String
    ): Call<FriendsList>

    @POST("api/v1/userFriendsList")
    fun createFriendsList(
        @Header("Authorization") authHeader: String,
        @Body friendListRequest: FriendListRequest
    ): Call<FriendsList>
    
    //PAYMENT
    @GET("api/v1/payments/userId/{userId}/status/PENDING")
    fun getPaymentsByUserId(
        @Header("Authorization") authHeader: String,
        @Path("userId") userId: Int): Call<List<Payment>>

    @GET("api/v1/expenses")
    fun getAllExpenses(
        @Header("Authorization") authHeader: String,): Call<List<Expense>>

    //AUTHENTICATION
    @POST("api/v1/authentication/sign-in")
    fun signIn(@Body signInRequest: SignInRequest): Call<AuthenticatedUserResource>

    @POST("api/v1/authentication/sign-up")
    fun signUp(@Body signUpRequest: SignUpRequest): Call<User>

}
