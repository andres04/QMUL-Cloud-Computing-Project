package qmul.marketplace

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.KoinApplication
import qmul.marketplace.data.dataModule
import qmul.marketplace.data.platformModule
import qmul.marketplace.domain.di.domainModule
import qmul.marketplace.navigation.Destination
import qmul.marketplace.navigation.NavGraph
import qmul.marketplace.navigation.components.MobileBottomBar
import qmul.marketplace.presentation.basket.BasketView
import qmul.marketplace.presentation.basket.basketModule
import qmul.marketplace.presentation.catalog.CatalogView
import qmul.marketplace.presentation.catalog.catalogModule
import qmul.marketplace.presentation.login.LoginView
import qmul.marketplace.presentation.login.loginModule
import qmul.marketplace.presentation.productdetail.ProductDetailView
import qmul.marketplace.presentation.productdetail.productDetailModule
import qmul.marketplace.presentation.signup.SignUpView
import qmul.marketplace.presentation.signup.signUpModule

/**
 * Consolidated list of Koin modules for global application dependency injection.
 */
val appModules = listOf(
    domainModule,
    dataModule,
    platformModule,
    catalogModule,
    loginModule,
    signUpModule,
    basketModule,
    productDetailModule
)

/**
 * Root entry point of the multiplatform application initializing Koin, Navigation orchestration, and single Material 3 NavigationBar on Mobile.
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
                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    val isMobile = maxWidth < 600.dp
                    val isTopLevelTab = destination is Destination.Catalog ||
                            destination is Destination.Search ||
                            destination is Destination.Basket ||
                            destination is Destination.Login

                    if (isMobile && isTopLevelTab) {
                        Scaffold(
                            contentWindowInsets = WindowInsets(0, 0, 0, 0),
                            bottomBar = {
                                MobileBottomBar(
                                    currentDestination = destination,
                                    onNavigate = onNavigate
                                )
                            }
                        ) { paddingValues ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(paddingValues)
                            ) {
                                RenderDestination(destination, onNavigate)
                            }
                        }
                    } else {
                        RenderDestination(destination, onNavigate)
                    }
                }
            }
        }
    }
}

@Composable
private fun RenderDestination(
    destination: Destination,
    onNavigate: (Destination) -> Unit
) {
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
        is Destination.SignUp -> {
            SignUpView(
                onNavigate = onNavigate
            )
        }
        is Destination.ProductDetail -> {
            ProductDetailView(
                productId = destination.productId,
                onNavigate = onNavigate
            )
        }
    }
}
