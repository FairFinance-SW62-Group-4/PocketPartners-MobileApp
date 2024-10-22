package Beans

data class Expense(
    val id: Long,
    val name: String,
    val amount: Double,
    val userInformationId: Long,
    val groupId: Long
)