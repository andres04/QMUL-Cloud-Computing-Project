package qmul.marketplace.presentation.login

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val loginModule = module {
    factoryOf(::LoginViewModel)
}
