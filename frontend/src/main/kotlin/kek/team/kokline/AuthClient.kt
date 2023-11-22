package kek.team.kokline

import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.header
import io.ktor.client.request.post
import kek.team.kokline.ClientFactory.baseApiUrl
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class AuthClient(private val client: HttpClient = ClientFactory.client) {

    @OptIn(ExperimentalEncodingApi::class)
    suspend fun login(username: String, password: String) {
        val response = client.post(LOGIN_URL) {
            header("Authorization", Base64.decode(username + password))
        }
    }

    suspend fun logout() {
        val response = client.delete(LOGOUT_URL)
    }

    companion object {
        private const val LOGIN_URL = "$baseApiUrl/auth/login"
        private const val LOGOUT_URL = "$baseApiUrl/auth/logout"
    }
}
