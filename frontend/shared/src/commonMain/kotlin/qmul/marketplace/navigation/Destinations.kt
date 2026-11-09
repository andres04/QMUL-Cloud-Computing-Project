package qmul.marketplace.navigation

import kotlinx.serialization.Serializable

/**
 * Type-safe navigation destinations for the application.
 */
sealed interface Destination {
    @Serializable
    data object Catalog : Destination

    @Serializable
    data object Search : Destination

    @Serializable
    data object Basket : Destination

    @Serializable
    data object Login : Destination

    @Serializable
    data object SignUp : Destination

    @Serializable
    data class ProductDetail(val productId: String) : Destination
}
