package qmul.marketplace.data

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import qmul.marketplace.data.network.AuthApi
import qmul.marketplace.data.network.ProductApi
import qmul.marketplace.data.repositories.AuthRepositoryImpl
import qmul.marketplace.domain.repository.AuthRepository

val dataModule = module {
    // Ktor Client with CIO engine
    single {
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        prettyPrint = true
                        isLenient = true
                    }
                )
            }
        }
    }

    // Network APIs
    single { ProductApi(get()) }
    single { AuthApi(get()) }

    // Repositories
    singleOf(::AuthRepositoryImpl) bind AuthRepository::class
}
