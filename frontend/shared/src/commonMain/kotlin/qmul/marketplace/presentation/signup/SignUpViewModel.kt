package qmul.marketplace.presentation.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import qmul.marketplace.domain.model.SignUpData
import qmul.marketplace.domain.usecase.SignUpUseCase

class SignUpViewModel(
    private val signUpUseCase: SignUpUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignUpState())
    val uiState: StateFlow<SignUpState> = _uiState.asStateFlow()

    fun onEvent(event: SignUpEvents) {
        when (event) {
            is SignUpEvents.OnUsernameChanged -> {
                _uiState.update { it.copy(username = event.username, errorMessage = null) }
            }
            is SignUpEvents.OnFirstNameChanged -> {
                _uiState.update { it.copy(firstName = event.firstName, errorMessage = null) }
            }
            is SignUpEvents.OnLastNameChanged -> {
                _uiState.update { it.copy(lastName = event.lastName, errorMessage = null) }
            }
            is SignUpEvents.OnEmailChanged -> {
                _uiState.update { it.copy(email = event.email, errorMessage = null) }
            }
            is SignUpEvents.OnPasswordChanged -> {
                _uiState.update { it.copy(password = event.password, errorMessage = null) }
            }
            is SignUpEvents.OnConfirmPasswordChanged -> {
                _uiState.update { it.copy(confirmPassword = event.confirmPassword, errorMessage = null) }
            }
            is SignUpEvents.OnContinueClicked -> {
                performSignUp()
            }
            is SignUpEvents.OnDismissError -> {
                _uiState.update { it.copy(errorMessage = null) }
            }
            SignUpEvents.OnBackClicked -> {
                // Handled via View navigation callbacks
            }
        }
    }

    private fun performSignUp() {
        val state = _uiState.value
        if (state.username.isBlank() || state.email.isBlank() || state.password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Username, email, and password are required") }
            return
        }
        if (state.password != state.confirmPassword) {
            _uiState.update { it.copy(errorMessage = "Passwords do not match") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val data = SignUpData(
                username = state.username.trim(),
                firstName = state.firstName.trim(),
                lastName = state.lastName.trim(),
                email = state.email.trim(),
                password = state.password,
                confirmPassword = state.confirmPassword
            )

            val result = signUpUseCase(data)
            result.fold(
                onSuccess = { user ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isSignedUp = true
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Failed to sign up"
                        )
                    }
                }
            )
        }
    }
}
