package Beans

data class Expense(
    val id: Long,
    val name: String,
    val amount: Double,
    val userId: Int,
    val groupId: Long,
    var groupName: String?=null
)