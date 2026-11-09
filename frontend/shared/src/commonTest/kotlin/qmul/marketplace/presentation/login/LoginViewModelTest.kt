package qmul.marketplace.presentation.login

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
import qmul.marketplace.domain.usecase.LoginUseCase

class FakeAuthRepository : AuthRepository {
    var shouldFail = false

    override suspend fun login(credentials: AuthCredentials): Result<User> {
        return if (shouldFail) {
            Result.failure(IllegalArgumentException("Invalid credentials"))
        } else {
            Result.success(User("usr_1", credentials.username, "token_123"))
        }
    }

    override suspend fun signUp(data: SignUpData): Result<User> {
        return Result.success(User("usr_1", data.username, "token_123"))
    }
}

class LoginViewModelTest {

    @Test
    fun login_with_empty_credentials_sets_error_message() = runTest {
        val fakeRepo = FakeAuthRepository()
        val loginUseCase = LoginUseCase(fakeRepo)
        val viewModel = LoginViewModel(loginUseCase)

        viewModel.onEvent(LoginEvents.OnLoginClicked)

        assertEquals("Please enter both username and password", viewModel.uiState.value.errorMessage)
        assertFalse(viewModel.uiState.value.isLoggedIn)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun login_with_valid_credentials_updates_isLoggedIn_state() = runTest {
        val testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)

        try {
            val fakeRepo = FakeAuthRepository()
            val loginUseCase = LoginUseCase(fakeRepo)
            val viewModel = LoginViewModel(loginUseCase)

            viewModel.onEvent(LoginEvents.OnUsernameChanged("testuser"))
            viewModel.onEvent(LoginEvents.OnPasswordChanged("password123"))
            viewModel.onEvent(LoginEvents.OnLoginClicked)

            testDispatcher.scheduler.advanceUntilIdle()

            assertTrue(viewModel.uiState.value.isLoggedIn)
            assertNull(viewModel.uiState.value.errorMessage)
        } finally {
            Dispatchers.resetMain()
        }
    }
}
