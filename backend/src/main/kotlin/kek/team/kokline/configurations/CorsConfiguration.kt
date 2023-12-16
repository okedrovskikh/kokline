package kek.team.kokline.configurations

import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.cors.routing.CORS

fun Application.configureCors() {
    install(CORS) {
        allowHost("localhost:5173")
        allowHost("localhost:4173")

        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Patch)
        allowMethod(HttpMethod.Delete)

        exposeHeader(HttpHeaders.AccessControlAllowOrigin)
        exposeHeader(HttpHeaders.ContentType)
        exposeHeader(HttpHeaders.Authorization)

        allowHeaders { true }
        allowNonSimpleContentTypes = true
        allowSameOrigin = true
        allowCredentials = true
    }
}