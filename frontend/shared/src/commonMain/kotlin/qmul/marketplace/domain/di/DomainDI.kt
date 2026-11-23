package qmul.marketplace.domain.di

import org.koin.dsl.module
import qmul.marketplace.domain.repository.AuthRepository
import qmul.marketplace.domain.repository.CartRepository
import qmul.marketplace.domain.repository.ProductRepository
import qmul.marketplace.domain.usecase.AddToCartUseCase
import qmul.marketplace.domain.usecase.ClearCartUseCase
import qmul.marketplace.domain.usecase.GetCartUseCase
import qmul.marketplace.domain.usecase.GetProductDetailUseCase
import qmul.marketplace.domain.usecase.GetProductsUseCase
import qmul.marketplace.domain.usecase.LoginUseCase
import qmul.marketplace.domain.usecase.RemoveFromCartUseCase
import qmul.marketplace.domain.usecase.SignUpUseCase
import qmul.marketplace.domain.usecase.UpdateCartQuantityUseCase

object DomainDI {
    fun provideGetProductsUseCase(productRepository: ProductRepository): GetProductsUseCase {
        return GetProductsUseCase(productRepository)
    }

    fun provideGetProductDetailUseCase(productRepository: ProductRepository): GetProductDetailUseCase {
        return GetProductDetailUseCase(productRepository)
    }

    fun provideLoginUseCase(authRepository: AuthRepository): LoginUseCase {
        return LoginUseCase(authRepository)
    }

    fun provideSignUpUseCase(authRepository: AuthRepository): SignUpUseCase {
        return SignUpUseCase(authRepository)
    }

    fun provideGetCartUseCase(cartRepository: CartRepository) = GetCartUseCase(cartRepository)
    fun provideAddToCartUseCase(cartRepository: CartRepository) = AddToCartUseCase(cartRepository)
    fun provideUpdateCartQuantityUseCase(cartRepository: CartRepository) = UpdateCartQuantityUseCase(cartRepository)
    fun provideRemoveFromCartUseCase(cartRepository: CartRepository) = RemoveFromCartUseCase(cartRepository)
    fun provideClearCartUseCase(cartRepository: CartRepository) = ClearCartUseCase(cartRepository)
}

val domainModule = module {
    factory { DomainDI.provideGetProductsUseCase(get()) }
    factory { DomainDI.provideGetProductDetailUseCase(get()) }
    factory { DomainDI.provideLoginUseCase(get()) }
    factory { DomainDI.provideSignUpUseCase(get()) }
    factory { DomainDI.provideGetCartUseCase(get()) }
    factory { DomainDI.provideAddToCartUseCase(get()) }
    factory { DomainDI.provideUpdateCartQuantityUseCase(get()) }
    factory { DomainDI.provideRemoveFromCartUseCase(get()) }
    factory { DomainDI.provideClearCartUseCase(get()) }
}
