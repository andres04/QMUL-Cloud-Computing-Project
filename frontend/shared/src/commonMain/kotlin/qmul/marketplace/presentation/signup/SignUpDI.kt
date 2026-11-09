package qmul.marketplace.presentation.signup

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val signUpModule = module {
    factoryOf(::SignUpViewModel)
}
