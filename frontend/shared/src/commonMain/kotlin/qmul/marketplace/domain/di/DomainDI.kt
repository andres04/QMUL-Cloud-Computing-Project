package qmul.marketplace.domain.di

import org.koin.dsl.module
import qmul.marketplace.domain.repository.AuthRepository
import qmul.marketplace.domain.repository.ProductRepository
import qmul.marketplace.domain.usecase.GetProductsUseCase
import qmul.marketplace.domain.usecase.LoginUseCase
import qmul.marketplace.domain.usecase.SignUpUseCase

object DomainDI {
    fun provideGetProductsUseCase(productRepository: ProductRepository): GetProductsUseCase {
        return GetProductsUseCase(productRepository)
    }

    fun provideLoginUseCase(authRepository: AuthRepository): LoginUseCase {
        return LoginUseCase(authRepository)
    }

    fun provideSignUpUseCase(authRepository: AuthRepository): SignUpUseCase {
        return SignUpUseCase(authRepository)
    }
}

val domainModule = module {
    factory { DomainDI.provideGetProductsUseCase(get()) }
    factory { DomainDI.provideLoginUseCase(get()) }
    factory { DomainDI.provideSignUpUseCase(get()) }
}
