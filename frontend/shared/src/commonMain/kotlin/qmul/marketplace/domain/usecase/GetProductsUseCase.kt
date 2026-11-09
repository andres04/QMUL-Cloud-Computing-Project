package qmul.marketplace.domain.usecase

import qmul.marketplace.domain.model.ProductFilter
import qmul.marketplace.domain.model.ProductPage
import qmul.marketplace.domain.repository.ProductRepository

class GetProductsUseCase(
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(
        query: String? = null,
        filter: ProductFilter? = null,
        sortBy: String? = null,
        page: Int = 1,
        pageSize: Int = 20
    ): Result<ProductPage> {
        return productRepository.getProducts(
            query = query,
            filter = filter,
            sortBy = sortBy,
            page = page,
            pageSize = pageSize
        )
    }
}
