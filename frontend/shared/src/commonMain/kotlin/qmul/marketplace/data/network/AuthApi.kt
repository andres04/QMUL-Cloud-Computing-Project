package qmul.marketplace.data.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class AuthApi(
    private val client: HttpClient,
    private val baseUrl: String = "https://api.example.com"
) {
    suspend fun login(request: LoginRequestDto): LoginResponseDto {
        return client.post("$baseUrl/api/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun signUp(request: SignUpRequestDto): LoginResponseDto {
        return client.post("$baseUrl/api/auth/signup") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
}
