package qmul.marketplace.presentation.signup

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import qmul.marketplace.domain.model.AuthCredentials
import qmul.marketplace.domain.model.SignUpData
import qmul.marketplace.domain.model.User
import qmul.marketplace.domain.repository.AuthRepository
import qmul.marketplace.domain.usecase.SignUpUseCase

class FakeSignUpAuthRepository : AuthRepository {
    override suspend fun login(credentials: AuthCredentials): Result<User> {
        return Result.success(User("usr_1", credentials.username, "token_123"))
    }

    override suspend fun signUp(data: SignUpData): Result<User> {
        return if (data.password != data.confirmPassword) {
            Result.failure(IllegalArgumentException("Passwords do not match"))
        } else {
            Result.success(User("usr_2", data.username, "token_signup_456"))
        }
    }
}

class SignUpViewModelTest {

    @Test
    fun signup_with_mismatched_passwords_sets_error_message() = runTest {
        val fakeRepo = FakeSignUpAuthRepository()
        val signUpUseCase = SignUpUseCase(fakeRepo)
        val viewModel = SignUpViewModel(signUpUseCase)

        viewModel.onEvent(SignUpEvents.OnUsernameChanged("johndoe"))
        viewModel.onEvent(SignUpEvents.OnEmailChanged("john@example.com"))
        viewModel.onEvent(SignUpEvents.OnPasswordChanged("password123"))
        viewModel.onEvent(SignUpEvents.OnConfirmPasswordChanged("different123"))
        viewModel.onEvent(SignUpEvents.OnContinueClicked)

        assertEquals("Passwords do not match", viewModel.uiState.value.errorMessage)
        assertFalse(viewModel.uiState.value.isSignedUp)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun signup_with_valid_data_updates_isSignedUp_state() = runTest {
        val testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)

        try {
            val fakeRepo = FakeSignUpAuthRepository()
            val signUpUseCase = SignUpUseCase(fakeRepo)
            val viewModel = SignUpViewModel(signUpUseCase)

            viewModel.onEvent(SignUpEvents.OnUsernameChanged("johndoe"))
            viewModel.onEvent(SignUpEvents.OnEmailChanged("john@example.com"))
            viewModel.onEvent(SignUpEvents.OnPasswordChanged("password123"))
            viewModel.onEvent(SignUpEvents.OnConfirmPasswordChanged("password123"))
            viewModel.onEvent(SignUpEvents.OnContinueClicked)

            testDispatcher.scheduler.advanceUntilIdle()

            assertTrue(viewModel.uiState.value.isSignedUp)
            assertNull(viewModel.uiState.value.errorMessage)
        } finally {
            Dispatchers.resetMain()
        }
    }
}
