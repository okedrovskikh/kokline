package kek.team.kokline.configurations

import io.ktor.server.application.Application
import io.ktor.server.application.install
import kek.team.kokline.mappers.mapperModule
import kek.team.kokline.persistence.persistenceModule
import kek.team.kokline.redis.redisModule
import kek.team.kokline.service.serviceModule
import org.koin.ktor.plugin.Koin

fun Application.configureKoin() {
    install(Koin) {
        modules(persistenceModule, mapperModule, serviceModule, jacksonModule, redisModule)
    }
}
