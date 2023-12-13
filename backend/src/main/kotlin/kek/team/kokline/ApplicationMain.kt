package kek.team.kokline

import io.ktor.http.*
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.netty.EngineMain
import io.ktor.server.plugins.cors.routing.CORS
import kek.team.kokline.configurations.configureAuth
import kek.team.kokline.configurations.configureDoubleReceive
import kek.team.kokline.configurations.configureSessions
import kek.team.kokline.configurations.configureExceptions
import kek.team.kokline.configurations.configureKoin
import kek.team.kokline.configurations.configureRouting
import kek.team.kokline.configurations.configureSerialization
import kek.team.kokline.configurations.configureSockets
import kek.team.kokline.factories.CoroutinePoolFactory
import kek.team.kokline.factories.KoinFactory
import kek.team.kokline.persistence.plugins.hikariPoolPlugin
import kek.team.kokline.persistence.plugins.migrations
import kek.team.kokline.redis.plugins.redisPlugin

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
    CoroutinePoolFactory.init(environment.config)
    install(hikariPoolPlugin)
    install(migrations)
    install(redisPlugin)
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
    configureKoin()
    KoinFactory.init(this)
    configureDoubleReceive()
    configureExceptions()
    configureSessions()
    configureAuth()
    configureSerialization()
    configureSockets()
    configureRouting()
}
