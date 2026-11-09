package qmul.marketplace.presentation.catalog

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import qmul.marketplace.domain.model.Product
import qmul.marketplace.navigation.Destination

/**
 * Main Stateless screen evaluating window size to render Desktop or Mobile layout.
 */
@Composable
fun CatalogScreen(
    state: CatalogState,
    onEvent: (CatalogEvents) -> Unit,
    onNavigate: (Destination) -> Unit = {},
    currentDestination: Destination = Destination.Catalog,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
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
                onEvent = onEvent
            )
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
