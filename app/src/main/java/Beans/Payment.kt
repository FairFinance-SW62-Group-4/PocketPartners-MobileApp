package Beans

data class Payment(
    val id: Long,
    val description: String,
    val amount: Double,
    val status: String,
    val userInformationId: Long,
    val expenseId: Long
)