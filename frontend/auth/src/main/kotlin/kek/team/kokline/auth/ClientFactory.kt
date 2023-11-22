package kek.team.kokline.auth

import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.Auth

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
