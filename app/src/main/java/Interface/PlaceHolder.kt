package Interface

import Beans.AuthenticatedUserResource
import Beans.Expense
import Beans.FriendsList
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
    @GET("api/v1/groups")
    fun getListadoGroups(): Call<List<Grupo>>

    @GET("api/v1/userFriendsList/userId/{userId}")
    fun getFriendsList(@Path("userId") userId: Int): Call<FriendsList>

    @GET("api/v1/usersInformation/userId/{userId}")
    fun getUserInformation(@Path("userId") userId: Int): Call<UsersInformation>

    @POST("api/v1/usersInformation")
    fun createUserInformation(
        @Header("Authorization") authHeader: String,
        @Body userInformationRequest: UserInformationRequest
    ): Call<UsersInformation>


    @POST("api/v1/groups")
    fun createGroup(@Body groupRequest: GroupRequest): Call<GroupResponse>

    @POST("api/v1/groups/{groupId}/members/{userId}")
    fun addMemberToGroup(@Path("groupId") groupId: Int, @Path("userId") userId: Int, @Body memberJson: Map<String, Any>): Call<Void>


    @GET("api/v1/payments/userId/{userId}")
    fun getPaymentsByUserId(@Path("userId") userId: Long): Call<List<Payment>>

    @GET("api/v1/expenses/groupId/{groupId}")
    fun getExpensesByGroupId(@Path("groupId") groupId: Long): Call<List<Expense>>

    @POST("api/v1/authentication/sign-in")
    fun signIn(@Body signInRequest: SignInRequest): Call<AuthenticatedUserResource>

    @POST("api/v1/authentication/sign-up")
    fun signUp(@Body signUpRequest: SignUpRequest): Call<User>

}