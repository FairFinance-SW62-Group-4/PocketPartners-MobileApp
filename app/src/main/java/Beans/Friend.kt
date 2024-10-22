package Beans

data class Friend (
    val id:Int,
    val fullName:String,
    val phoneNumber:String,
    val photo:String,
    val email:String,
    val userId:Int
)

data class FriendsOfUser(
    val id:Int,
    val userId: Int,
    val friendIds: List<Int>
)

data class FriendListRequest(
    val userId: Int
)