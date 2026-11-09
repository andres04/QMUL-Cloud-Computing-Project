package qmul.marketplace.presentation.signup

sealed interface SignUpEvents {
    data class OnUsernameChanged(val username: String) : SignUpEvents
    data class OnFirstNameChanged(val firstName: String) : SignUpEvents
    data class OnLastNameChanged(val lastName: String) : SignUpEvents
    data class OnEmailChanged(val email: String) : SignUpEvents
    data class OnPasswordChanged(val password: String) : SignUpEvents
    data class OnConfirmPasswordChanged(val confirmPassword: String) : SignUpEvents
    data object OnContinueClicked : SignUpEvents
    data object OnBackClicked : SignUpEvents
    data object OnDismissError : SignUpEvents
}
