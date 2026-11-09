package qmul.marketplace.domain.di

import org.koin.dsl.module
import qmul.marketplace.domain.repository.ProductRepository
import qmul.marketplace.domain.usecase.GetProductsUseCase

object DomainDI {
    fun provideGetProductsUseCase(productRepository: ProductRepository): GetProductsUseCase {
        return GetProductsUseCase(productRepository)
    }
}

val domainModule = module {
    factory { DomainDI.provideGetProductsUseCase(get()) }
}
