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