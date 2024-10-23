package Beans

data class Expense(
    val id: Long,
    val name: String,
    val amount: Double,
    val userInformationId: Long,
    val groupId: Long
)

data class ExpenseResponse(
    val id: Long,
    val name: String,
    val amount: Double,
    val userId: Long,
    val groupId: Long,
    val createdAt: String,
    val updatedAt: String
)