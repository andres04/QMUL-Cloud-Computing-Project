package qmul.marketplace.domain.model

data class ProductPage(
    val products: List<Product>,
    val currentPage: Int,
    val totalPages: Int,
    val totalItems: Int
)
