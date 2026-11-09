package qmul.marketplace.data

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module
import qmul.marketplace.data.network.ProductApi

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

    // Network API explicit factory (avoids Koin trying to resolve String for default baseUrl)
    single { ProductApi(get()) }
}
