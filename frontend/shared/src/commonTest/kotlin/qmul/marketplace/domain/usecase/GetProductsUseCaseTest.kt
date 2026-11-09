package qmul.marketplace.domain.usecase

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest
import qmul.marketplace.domain.model.Product
import qmul.marketplace.domain.model.ProductFilter
import qmul.marketplace.domain.model.ProductPage
import qmul.marketplace.domain.repository.ProductRepository

class FakeProductRepository : ProductRepository {
    override suspend fun getProducts(
        query: String?,
        filter: ProductFilter?,
        sortBy: String?,
        page: Int,
        pageSize: Int
    ): Result<ProductPage> {
        val products = listOf(
            Product("1", "Speckled Stoneware Mug", 28.00, 5.0, "", "Ceramics"),
            Product("2", "Linen Throw Blanket", 74.00, 4.0, "", "Home & living")
        )
        return Result.success(ProductPage(products = products, currentPage = 1, totalPages = 1, totalItems = 2))
    }
}

class GetProductsUseCaseTest {

    @Test
    fun getProducts_returns_successful_product_page() = runTest {
        val fakeRepo = FakeProductRepository()
        val useCase = GetProductsUseCase(fakeRepo)

        val result = useCase()

        assertTrue(result.isSuccess)
        val page = result.getOrNull()
        assertEquals(2, page?.products?.size)
        assertEquals("Speckled Stoneware Mug", page?.products?.first()?.title)
    }
}
