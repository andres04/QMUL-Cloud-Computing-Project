package qmul.marketplace.domain.model

data class ProductFilter(
    val category: String? = null,
    val minPrice: Double? = null,
    val maxPrice: Double? = null,
    val rating: Double? = null,
    val location: String? = null
)
