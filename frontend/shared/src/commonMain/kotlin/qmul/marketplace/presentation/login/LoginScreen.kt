package qmul.marketplace.presentation.login

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import qmul.marketplace.navigation.Destination

/**
 * Main Stateless Login screen selecting between Mobile and Desktop layout at breakpoint 600.dp.
 */
@Composable
fun LoginScreen(
    state: LoginState,
    onEvent: (LoginEvents) -> Unit,
    onNavigate: (Destination) -> Unit = {},
    currentDestination: Destination = Destination.Login,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        if (maxWidth < 600.dp) {
            LoginMobileContent(
                state = state,
                onEvent = onEvent,
                onNavigate = onNavigate,
                currentDestination = currentDestination
            )
        } else {
            LoginDesktopContent(
                state = state,
                onEvent = onEvent,
                onNavigate = onNavigate
            )
        }
    }
}

@Preview
@Composable
private fun LoginScreenPreview() {
    MaterialTheme {
        LoginScreen(
            state = LoginState(),
            onEvent = {}
        )
    }
}
