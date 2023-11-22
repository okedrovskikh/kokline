package kek.team.kokline

import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BasicAuthCredentials
import io.ktor.client.plugins.auth.providers.basic

object ClientFactory {
    const val baseApiUrl: String = "/api/v1"
    val client: HttpClient = HttpClient {
        install(Auth) {
//            basic {
//                credentials {
//                    BasicAuthCredentials()
//                }
//            }
        }
    }

    fun close() {
        client.close()
    }
}
