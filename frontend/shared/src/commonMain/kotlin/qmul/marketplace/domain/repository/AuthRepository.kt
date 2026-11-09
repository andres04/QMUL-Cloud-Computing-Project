package qmul.marketplace.domain.repository

import qmul.marketplace.domain.model.AuthCredentials
import qmul.marketplace.domain.model.SignUpData
import qmul.marketplace.domain.model.User

interface AuthRepository {
    suspend fun login(credentials: AuthCredentials): Result<User>
    suspend fun signUp(data: SignUpData): Result<User>
}
