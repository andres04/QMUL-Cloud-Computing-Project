package qmul.marketplace.presentation.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import qmul.marketplace.navigation.Destination

/**
 * Stateful bridge obtaining LoginViewModel via Koin and connecting state/events to LoginScreen.
 */
@Composable
fun LoginView(
    modifier: Modifier = Modifier,
    onNavigate: (Destination) -> Unit = {},
    currentDestination: Destination = Destination.Login,
    viewModel: LoginViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) {
            onNavigate(Destination.Catalog)
        }
    }

    LoginScreen(
        state = uiState,
        onEvent = { event ->
            when (event) {
                is LoginEvents.OnBackClicked -> onNavigate(Destination.Catalog)
                is LoginEvents.OnSignUpClicked -> onNavigate(Destination.SignUp)
                else -> viewModel.onEvent(event)
            }
        },
        onNavigate = onNavigate,
        currentDestination = currentDestination,
        modifier = modifier
    )
}
