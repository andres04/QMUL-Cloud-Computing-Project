package qmul.marketplace.presentation.signup

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import qmul.marketplace.navigation.Destination

/**
 * Stateful bridge obtaining SignUpViewModel via Koin and connecting state/events to SignUpScreen.
 */
@Composable
fun SignUpView(
    modifier: Modifier = Modifier,
    onNavigate: (Destination) -> Unit = {},
    viewModel: SignUpViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isSignedUp) {
        if (uiState.isSignedUp) {
            onNavigate(Destination.Catalog)
        }
    }

    SignUpScreen(
        state = uiState,
        onEvent = { event ->
            when (event) {
                is SignUpEvents.OnBackClicked -> onNavigate(Destination.Login)
                else -> viewModel.onEvent(event)
            }
        },
        modifier = modifier
    )
}
