package qmul.marketplace.domain.repository

import qmul.marketplace.domain.model.ProductFilter
import qmul.marketplace.domain.model.ProductPage

interface ProductRepository {
    suspend fun getProducts(
        query: String? = null,
        filter: ProductFilter? = null,
        sortBy: String? = null,
        page: Int = 1,
        pageSize: Int = 20
    ): Result<ProductPage>
}
