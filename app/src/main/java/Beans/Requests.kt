package Beans

data class SignInRequest(
    val username: String,
    val password: String
)

data class SignUpRequest(
    val username: String,
    val password: String,
    val roles: List<String>
)