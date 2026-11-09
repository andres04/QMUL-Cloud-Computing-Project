package qmul.marketplace.data.repositories

import qmul.marketplace.data.network.AuthApi
import qmul.marketplace.domain.model.AuthCredentials
import qmul.marketplace.domain.model.SignUpData
import qmul.marketplace.domain.model.User
import qmul.marketplace.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val authApi: AuthApi
) : AuthRepository {

    private var savedToken: String? = null
    private var currentUser: User? = null

    override suspend fun login(credentials: AuthCredentials): Result<User> {
        return runCatching {
            // TODO: Replace mock login with remote REST service call: authApi.login(LoginRequestDto(credentials.username, credentials.password))
            if (credentials.username.isBlank() || credentials.password.isBlank()) {
                throw IllegalArgumentException("Username and password cannot be empty")
            }

            val mockUser = User(
                id = "usr_101",
                username = credentials.username,
                token = "mock_jwt_token_${credentials.username}_12345"
            )

            savedToken = mockUser.token
            currentUser = mockUser
            mockUser
        }
    }

    override suspend fun signUp(data: SignUpData): Result<User> {
        return runCatching {
            // TODO: Replace mock sign up with remote REST service call: authApi.signUp(SignUpRequestDto(...))
            if (data.username.isBlank() || data.email.isBlank() || data.password.isBlank()) {
                throw IllegalArgumentException("Username, email, and password are required")
            }
            if (data.password != data.confirmPassword) {
                throw IllegalArgumentException("Passwords do not match")
            }

            val newUser = User(
                id = "usr_" + (102..999).random(),
                username = data.username,
                token = "mock_jwt_signup_${data.username}_token"
            )

            savedToken = newUser.token
            currentUser = newUser
            newUser
        }
    }

    fun getSavedToken(): String? = savedToken
}
