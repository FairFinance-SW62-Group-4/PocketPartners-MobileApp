package Beans

data class User(
    val id: Int,
    val username: String,
    val roles: List<String>
)

data class UsersInformation(
    val id: Int,
    val fullName: String,
    val phoneNumber: String,
    val photo: String,
    val email: String,
    val userId: Int
)

data class FriendsList(
    val userId: Int,
    val friendsIds: List<Int>
)

data class AuthenticatedUserResource(
    val id: Long,
    val username: String,
    val token: String
)

data class SignInRequest(
    val username: String,
    val password: String
)

data class SignUpRequest(
    val username: String,
    val password: String,
    val roles: List<String>
)

data class UserInformationRequest(
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val photo: String,
    val email: String,
    val userId: Int
)
