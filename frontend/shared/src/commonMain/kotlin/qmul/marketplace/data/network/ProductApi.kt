package qmul.marketplace.data.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class ProductApi(
    private val client: HttpClient,
    private val baseUrl: String = "https://api.example.com"
) {
    suspend fun fetchProducts(
        query: String? = null,
        category: String? = null,
        minPrice: Double? = null,
        maxPrice: Double? = null,
        rating: Double? = null,
        location: String? = null,
        sortBy: String? = null,
        page: Int = 1,
        pageSize: Int = 20
    ): ProductPageResponseDto {
        return client.get("$baseUrl/api/products") {
            query?.let { parameter("query", it) }
            category?.let { parameter("category", it) }
            minPrice?.let { parameter("minPrice", it) }
            maxPrice?.let { parameter("maxPrice", it) }
            rating?.let { parameter("rating", it) }
            location?.let { parameter("location", it) }
            sortBy?.let { parameter("sortBy", it) }
            parameter("page", page)
            parameter("pageSize", pageSize)
        }.body()
    }
}
