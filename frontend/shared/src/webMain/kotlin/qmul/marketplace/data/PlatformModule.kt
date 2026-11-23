package qmul.marketplace.data

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import qmul.marketplace.data.repositories.WebProductRepoImpl
import qmul.marketplace.domain.repository.ProductRepository

actual val platformModule = module {
    singleOf(::WebProductRepoImpl) bind ProductRepository::class
}
