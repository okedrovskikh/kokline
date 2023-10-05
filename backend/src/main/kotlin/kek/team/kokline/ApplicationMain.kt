package kek.team.kokline

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.netty.EngineMain
import kek.team.kokline.configurations.configureKoin
import kek.team.kokline.configurations.configureRouting
import kek.team.kokline.configurations.configureSerialization
import kek.team.kokline.configurations.configureSockets
import kek.team.kokline.persistence.plugins.hikariPoolPlugin
import kek.team.kokline.persistence.plugins.migrations
import kek.team.kokline.redis.plugins.redisPlugin

fun main(args: Array<String>) = EngineMain.main(args)

// Надо настроить падение при ошибках инициализации плагинов
fun Application.module() {
    install(hikariPoolPlugin)
    install(migrations)
    install(redisPlugin)
    configureKoin()
    configureSerialization()
    configureSockets()
    configureRouting()
}
