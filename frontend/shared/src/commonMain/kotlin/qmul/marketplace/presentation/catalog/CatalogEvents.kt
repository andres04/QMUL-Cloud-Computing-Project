package qmul.marketplace.presentation.catalog

import qmul.marketplace.domain.model.Product
import qmul.marketplace.domain.model.ProductFilter

sealed interface CatalogEvents {
    data class OnSearch(val query: String) : CatalogEvents
    data class OnFilterApplied(val filter: ProductFilter) : CatalogEvents
    data class OnCategorySelected(val category: String?) : CatalogEvents
    data class OnAddToCart(val product: Product) : CatalogEvents
    data class OnPageChanged(val page: Int) : CatalogEvents
    data class OnSortByChanged(val sortBy: String) : CatalogEvents
    data object OnDismissError : CatalogEvents
    data object OnRefresh : CatalogEvents
}
