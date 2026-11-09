package qmul.marketplace.domain.usecase

import qmul.marketplace.domain.model.SignUpData
import qmul.marketplace.domain.model.User
import qmul.marketplace.domain.repository.AuthRepository

class SignUpUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(data: SignUpData): Result<User> {
        return authRepository.signUp(data)
    }
}
