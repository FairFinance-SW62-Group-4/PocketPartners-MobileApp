package Beans

data class Grupo(
    val id: Int,
    val name: String,
    val currency: List<Moneda>,
    val groupPhoto: String,
    val createdAt: String,
    val updatedAt: String
)

data class GroupJoin(
    val groupId: Int,
    val userId: Int,
    val joinedAt: String
)

data class Moneda(
    val id: Int,
    val code: String
)

data class GroupResponse(
    val id: Int,
    val name: String,
    val groupPhoto: String,
    val currency: List<String>
)

data class GroupRequest(
    val name: String,
    val groupPhoto: String,
    val currency: List<String>
)