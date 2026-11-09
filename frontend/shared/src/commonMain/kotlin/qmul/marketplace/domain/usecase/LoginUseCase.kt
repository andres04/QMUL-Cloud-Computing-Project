package qmul.marketplace.domain.usecase

import qmul.marketplace.domain.model.AuthCredentials
import qmul.marketplace.domain.model.User
import qmul.marketplace.domain.repository.AuthRepository

class LoginUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(credentials: AuthCredentials): Result<User> {
        return authRepository.login(credentials)
    }
}
