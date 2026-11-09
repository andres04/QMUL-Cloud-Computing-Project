package qmul.marketplace.presentation.catalog

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val catalogModule = module {
    factoryOf(::CatalogViewModel)
}
