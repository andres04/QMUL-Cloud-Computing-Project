package qmul.marketplace.presentation.catalog

import qmul.marketplace.domain.model.Product
import qmul.marketplace.domain.model.ProductFilter

data class CatalogState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val userMessage: String? = null,
    val searchQuery: String = "",
    val savedSearchQuery: String = "",
    val selectedCategory: String? = null,
    val customFilter: ProductFilter? = null,
    val appliedFilter: ProductFilter = ProductFilter(),
    val sortBy: String = "Most popular",
    val currentPage: Int = 1,
    val totalPages: Int = 1,
    val totalItems: Int = 0,
    val maxAvailablePrice: Double = 300.0,
    val cartItems: List<Product> = emptyList()
)
