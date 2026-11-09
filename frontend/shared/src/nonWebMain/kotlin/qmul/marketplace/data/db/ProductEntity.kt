package qmul.marketplace.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import qmul.marketplace.domain.model.Product

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String,
    val title: String,
    val price: Double,
    val rating: Double,
    val imageUrl: String,
    val category: String
) {
    fun toDomain(): Product = Product(
        id = id,
        title = title,
        price = price,
        rating = rating,
        imageUrl = imageUrl,
        category = category
    )

    companion object {
        fun fromDomain(product: Product): ProductEntity = ProductEntity(
            id = product.id,
            title = product.title,
            price = product.price,
            rating = product.rating,
            imageUrl = product.imageUrl,
            category = product.category
        )
    }
}
