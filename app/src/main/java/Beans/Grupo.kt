package Beans

data class Grupo(
    val id: Int,
    val name: String,
    val currency: List<Moneda>,
    val groupPhoto: String,
    val createdAt: String,
    val updatedAt: String
)

data class Moneda(
    val id: Int,
    val code: String
)