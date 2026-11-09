package qmul.marketplace.data

import kotlinx.coroutines.flow.flowOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import qmul.marketplace.data.db.AppDatabase
import qmul.marketplace.data.db.ProductDao
import qmul.marketplace.data.db.ProductEntity
import qmul.marketplace.data.repositories.ProductRepoImpl
import qmul.marketplace.domain.repository.ProductRepository

val platformDataModule = module {
    single<ProductDao> {
        val db = getOrNull<AppDatabase>()
        db?.productDao() ?: object : ProductDao {
            override fun getAllProductsFlow() = flowOf(emptyList<ProductEntity>())
            override suspend fun getAllProducts() = emptyList<ProductEntity>()
            override suspend fun getProductsByCategory(category: String?) = emptyList<ProductEntity>()
            override suspend fun insertProducts(products: List<ProductEntity>) {}
            override suspend fun clearProducts() {}
        }
    }
    singleOf(::ProductRepoImpl) bind ProductRepository::class
}
