package qmul.marketplace.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import qmul.marketplace.domain.model.AuthCredentials
import qmul.marketplace.domain.usecase.LoginUseCase

class LoginViewModel(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginState())
    val uiState: StateFlow<LoginState> = _uiState.asStateFlow()

    fun onEvent(event: LoginEvents) {
        when (event) {
            is LoginEvents.OnUsernameChanged -> {
                _uiState.update { it.copy(username = event.username, errorMessage = null) }
            }
            is LoginEvents.OnPasswordChanged -> {
                _uiState.update { it.copy(password = event.password, errorMessage = null) }
            }
            is LoginEvents.OnLoginClicked -> {
                performLogin()
            }
            is LoginEvents.OnDismissError -> {
                _uiState.update { it.copy(errorMessage = null) }
            }
            LoginEvents.OnSignUpClicked,
            LoginEvents.OnBackClicked -> {
                // Handled via View callbacks
            }
        }
    }

    private fun performLogin() {
        val currentState = _uiState.value
        if (currentState.username.isBlank() || currentState.password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please enter both username and password") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val credentials = AuthCredentials(
                username = currentState.username.trim(),
                password = currentState.password.trim()
            )

            val result = loginUseCase(credentials)
            result.fold(
                onSuccess = { user ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isLoggedIn = true
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Invalid login credentials"
                        )
                    }
                }
            )
        }
    }
}
