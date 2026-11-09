package qmul.marketplace.presentation.login

sealed interface LoginEvents {
    data class OnUsernameChanged(val username: String) : LoginEvents
    data class OnPasswordChanged(val password: String) : LoginEvents
    data object OnLoginClicked : LoginEvents
    data object OnSignUpClicked : LoginEvents
    data object OnBackClicked : LoginEvents
    data object OnDismissError : LoginEvents
}
