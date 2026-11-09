package qmul.marketplace.domain.model

data class Product(
    val id: String,
    val title: String,
    val price: Double,
    val rating: Double,
    val imageUrl: String,
    val category: String
)
