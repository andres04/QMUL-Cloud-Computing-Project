package qmul.marketplace.presentation.catalog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import qmul.marketplace.navigation.Destination

/**
 * Stateful bridge obtaining ViewModel via Koin and binding state/events to CatalogScreen.
 */
@Composable
fun CatalogView(
    onNavigate: (Destination) -> Unit = {},
    currentDestination: Destination = Destination.Catalog,
    modifier: Modifier = Modifier,
    viewModel: CatalogViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CatalogScreen(
        state = uiState,
        onEvent = viewModel::onEvent,
        onNavigate = onNavigate,
        currentDestination = currentDestination,
        modifier = modifier
    )
}
