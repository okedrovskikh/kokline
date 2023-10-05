package kek.team.kokline.redis.plugins

import io.ktor.server.application.ApplicationStarted
import io.ktor.server.application.ApplicationStopped
import io.ktor.server.application.createApplicationPlugin
import io.ktor.server.application.hooks.MonitoringEvent
import io.ktor.server.application.log
import kek.team.kokline.factories.RedisFactory

val redisPlugin = createApplicationPlugin("redis-plugin") {
    on(MonitoringEvent(ApplicationStarted)) {
        try {
            RedisFactory.init(it.environment.config)
        } catch (e: Throwable) {
            it.log.error("Error during redis initialization", e)
        }
    }

    on(MonitoringEvent(ApplicationStopped)) {
        RedisFactory.close()
    }
}
