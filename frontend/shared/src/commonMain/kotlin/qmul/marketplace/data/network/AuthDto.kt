package qmul.marketplace.data.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import qmul.marketplace.domain.model.User

@Serializable
data class LoginRequestDto(
    @SerialName("username") val username: String,
    @SerialName("password") val password: String
)

@Serializable
data class SignUpRequestDto(
    @SerialName("username") val username: String,
    @SerialName("firstName") val firstName: String,
    @SerialName("lastName") val lastName: String,
    @SerialName("email") val email: String,
    @SerialName("password") val password: String
)

@Serializable
data class LoginResponseDto(
    @SerialName("id") val id: String,
    @SerialName("username") val username: String,
    @SerialName("token") val token: String
) {
    fun toDomain(): User = User(
        id = id,
        username = username,
        token = token
    )
}
