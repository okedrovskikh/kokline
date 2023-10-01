package kek.team.kokline.configurations

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.serialization.jackson.jackson
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation

val objectMapper = jacksonObjectMapper()

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        jackson()
    }
}
