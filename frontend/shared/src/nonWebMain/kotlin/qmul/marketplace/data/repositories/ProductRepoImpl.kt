package qmul.marketplace.data.repositories

import qmul.marketplace.data.db.ProductDao
import qmul.marketplace.data.db.ProductEntity
import qmul.marketplace.data.network.ProductApi
import qmul.marketplace.domain.model.Product
import qmul.marketplace.domain.model.ProductFilter
import qmul.marketplace.domain.model.ProductPage
import qmul.marketplace.domain.repository.ProductRepository

class ProductRepoImpl(
    private val productApi: ProductApi,
    private val productDao: ProductDao
) : ProductRepository {

    // TODO: Replace mock products once the remote REST API service is fully deployed and accessible at /api/products
    private val mockProducts = listOf(
        Product("1", "Speckled Stoneware Mug", 28.00, 5.0, "", "Ceramics"),
        Product("2", "Linen Throw Blanket", 74.00, 4.0, "", "Home & living"),
        Product("3", "Riso Print — Coastal Fog", 42.00, 5.0, "", "Prints"),
        Product("4", "Brass Hoop Earrings", 36.00, 4.0, "", "Jewellery"),
        Product("5", "Walnut Serving Board", 58.00, 5.0, "", "Ceramics"),
        Product("6", "Hand-dyed Indigo Scarf", 49.00, 4.0, "", "Vintage"),
        Product("7", "Ceramic Planter — Large", 64.00, 5.0, "", "Ceramics"),
        Product("8", "Vintage Levi's 501 — W32", 88.00, 4.0, "", "Vintage")
    )

    override suspend fun getProducts(
        query: String?,
        filter: ProductFilter?,
        sortBy: String?,
        page: Int,
        pageSize: Int
    ): Result<ProductPage> {
        return runCatching {
            // TODO: Connect to REST API using productApi.fetchProducts(...) instead of returning mock dataset
            val filteredProducts = mockProducts.filter { product ->
                val matchesQuery = query.isNullOrBlank() || product.title.contains(query, ignoreCase = true)
                val matchesCategory = filter?.category.isNullOrBlank() ||
                        product.category.equals(filter.category, ignoreCase = true) ||
                        (filter.category == "Prints & posters" && product.category == "Prints")
                val matchesMinPrice = filter?.minPrice == null || product.price >= filter.minPrice
                val matchesMaxPrice = filter?.maxPrice == null || product.price <= filter.maxPrice
                val matchesRating = filter?.rating == null || product.rating >= filter.rating
                matchesQuery && matchesCategory && matchesMinPrice && matchesMaxPrice && matchesRating
            }

            // Save mock products to local database cache
            productDao.insertProducts(filteredProducts.map { ProductEntity.fromDomain(it) })

            ProductPage(
                products = filteredProducts,
                currentPage = page,
                totalPages = 1,
                totalItems = filteredProducts.size
            )
        }
    }
}
