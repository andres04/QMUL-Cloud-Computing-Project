package qmul.marketplace

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import org.koin.compose.KoinApplication
import qmul.marketplace.data.dataModule
import qmul.marketplace.data.platformModule
import qmul.marketplace.domain.di.domainModule
import qmul.marketplace.navigation.Destination
import qmul.marketplace.navigation.NavGraph
import qmul.marketplace.presentation.basket.BasketView
import qmul.marketplace.presentation.catalog.CatalogView
import qmul.marketplace.presentation.catalog.catalogModule
import qmul.marketplace.presentation.login.LoginView

/**
 * Consolidated list of Koin modules for global application dependency injection.
 */
val appModules = listOf(
    domainModule,
    dataModule,
    platformModule,
    catalogModule
)

/**
 * Root entry point of the multiplatform application initializing Koin and Navigation orchestration.
 */
@Composable
fun App() {
    KoinApplication(application = {
        modules(appModules)
    }) {
        MaterialTheme {
            NavGraph(
                initialDestination = Destination.Catalog
            ) { destination, onNavigate ->
                when (destination) {
                    is Destination.Catalog,
                    is Destination.Search -> {
                        CatalogView(
                            onNavigate = onNavigate,
                            currentDestination = destination
                        )
                    }
                    is Destination.Basket -> {
                        BasketView(
                            onNavigate = onNavigate,
                            currentDestination = destination
                        )
                    }
                    is Destination.Login -> {
                        LoginView(
                            onNavigate = onNavigate,
                            currentDestination = destination
                        )
                    }
                    is Destination.ProductDetail -> {
                        // ProductDetailView(destination.productId)
                    }
                }
            }
        }
    }
}
