package Beans

data class Payment(
    val id: Long,
    val description: String,
    val amount: Double,
    val status: String,
    val userInformationId: Long,
    val expenseId: Long
)

data class AddPayment(
    val description: String,
    val amount: Double,
    val userInformationId: Long,
    val expenseId: Long
)

data class PaymentCompleted(
    val message: String
)