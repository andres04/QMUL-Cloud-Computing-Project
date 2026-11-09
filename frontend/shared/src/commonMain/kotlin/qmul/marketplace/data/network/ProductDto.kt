package qmul.marketplace.data.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import qmul.marketplace.domain.model.Product

@Serializable
data class ProductDto(
    @SerialName("id") val id: String,
    @SerialName("title") val title: String,
    @SerialName("price") val price: Double,
    @SerialName("rating") val rating: Double,
    @SerialName("imageUrl") val imageUrl: String,
    @SerialName("category") val category: String
) {
    fun toDomain(): Product = Product(
        id = id,
        title = title,
        price = price,
        rating = rating,
        imageUrl = imageUrl,
        category = category
    )
}

@Serializable
data class ProductPageResponseDto(
    @SerialName("products") val products: List<ProductDto>,
    @SerialName("currentPage") val currentPage: Int = 1,
    @SerialName("totalPages") val totalPages: Int = 1,
    @SerialName("totalItems") val totalItems: Int = products.size
)
