package qmul.marketplace.presentation.catalog

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import qmul.marketplace.domain.model.Product
import qmul.marketplace.navigation.Destination

/**
 * Main Stateless screen evaluating window size to render Desktop or Mobile layout with quick Snackbar feedback and direct "View Basket" navigation action.
 */
@Composable
fun CatalogScreen(
    state: CatalogState,
    onEvent: (CatalogEvents) -> Unit,
    onNavigate: (Destination) -> Unit = {},
    currentDestination: Destination = Destination.Catalog,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.userMessage) {
        state.userMessage?.let { message ->
            val result = snackbarHostState.showSnackbar(
                message = message,
                actionLabel = "View Basket",
                duration = SnackbarDuration.Short
            )
            if (result == SnackbarResult.ActionPerformed) {
                onNavigate(Destination.Basket)
            }
            onEvent(CatalogEvents.OnUserMessageConsumed)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = Color(0xFF0F172A),
                    contentColor = Color.White,
                    actionColor = Color(0xFF38BDF8),
                    shape = RoundedCornerShape(8.dp)
                )
            }
        }
    ) { _ ->
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize()
        ) {
            if (maxWidth < 600.dp) {
                CatalogMobileContent(
                    state = state,
                    onEvent = onEvent,
                    onNavigate = onNavigate,
                    currentDestination = currentDestination
                )
            } else {
                CatalogDesktopContent(
                    state = state,
                    onEvent = onEvent,
                    onNavigate = onNavigate
                )
            }
        }
    }
}

@Preview
@Composable
private fun CatalogScreenDesktopPreview() {
    val sampleProducts = listOf(
        Product("1", "Speckled Stoneware Mug", 28.00, 5.0, "", "Ceramics"),
        Product("2", "Linen Throw Blanket", 74.00, 4.0, "", "Home & living")
    )
    MaterialTheme {
        CatalogScreen(
            state = CatalogState(products = sampleProducts),
            onEvent = {}
        )
    }
}
