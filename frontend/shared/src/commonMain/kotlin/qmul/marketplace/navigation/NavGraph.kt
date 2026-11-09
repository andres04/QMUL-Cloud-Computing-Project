package qmul.marketplace.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

/**
 * Type-safe Navigation graph orchestrator for Compose Multiplatform.
 */
@Composable
fun NavGraph(
    initialDestination: Destination = Destination.Catalog,
    content: @Composable (destination: Destination, onNavigate: (Destination) -> Unit) -> Unit
) {
    var currentDestination by remember { mutableStateOf(initialDestination) }
    content(currentDestination) { newDestination ->
        currentDestination = newDestination
    }
}
