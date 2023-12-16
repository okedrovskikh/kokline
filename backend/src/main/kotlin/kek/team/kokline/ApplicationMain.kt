package kek.team.kokline

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.netty.EngineMain
import kek.team.kokline.configurations.configureAuth
import kek.team.kokline.configurations.configureCors
import kek.team.kokline.configurations.configureDoubleReceive
import kek.team.kokline.configurations.configureSessions
import kek.team.kokline.configurations.configureExceptions
import kek.team.kokline.configurations.configureKoin
import kek.team.kokline.configurations.configureRouting
import kek.team.kokline.configurations.configureSerialization
import kek.team.kokline.configurations.configureSockets
import kek.team.kokline.factories.CoroutinePoolFactory
import kek.team.kokline.persistence.plugins.hikariPoolPlugin
import kek.team.kokline.persistence.plugins.migrations
import kek.team.kokline.redis.plugins.redisPlugin

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
    CoroutinePoolFactory.init(environment.config)
    install(hikariPoolPlugin)
    install(migrations)
    install(redisPlugin)
    configureCors()
    configureKoin()
    configureDoubleReceive()
    configureExceptions()
    configureSessions()
    configureAuth()
    configureSerialization()
    configureSockets()
    configureRouting()
}
