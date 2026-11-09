package qmul.marketplace.presentation.signup

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Main Stateless Sign Up screen selecting Mobile or Desktop layout at breakpoint 600.dp.
 */
@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier,
    state: SignUpState,
    onEvent: (SignUpEvents) -> Unit
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        if (maxWidth < 600.dp) {
            SignUpMobileContent(
                state = state,
                onEvent = onEvent
            )
        } else {
            SignUpDesktopContent(
                state = state,
                onEvent = onEvent
            )
        }
    }
}

@Preview
@Composable
private fun SignUpScreenPreview() {
    MaterialTheme {
        SignUpScreen(
            state = SignUpState(),
            onEvent = {}
        )
    }
}
