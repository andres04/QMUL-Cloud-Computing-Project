package qmul.marketplace.presentation.basket

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.koinInject
import qmul.marketplace.navigation.Destination

/**
 * Stateful bridge obtaining BasketViewModel via Koin and connecting state/events to BasketScreen.
 */
@Composable
fun BasketView(
    modifier: Modifier = Modifier,
    onNavigate: (Destination) -> Unit = {},
    currentDestination: Destination = Destination.Basket,
    viewModel: BasketViewModel = koinInject()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    BasketScreen(
        state = uiState,
        onEvent = { event ->
            when (event) {
                is BasketEvents.OnContinueShopping -> onNavigate(Destination.Catalog)
                else -> viewModel.onEvent(event)
            }
        },
        onNavigate = onNavigate,
        currentDestination = currentDestination,
        modifier = modifier
    )
}
