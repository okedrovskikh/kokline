package kek.team.kokline.redis.plugins

import io.ktor.server.application.ApplicationStopped
import io.ktor.server.application.createApplicationPlugin
import io.ktor.server.application.hooks.MonitoringEvent
import kek.team.kokline.factories.RedisFactory

val redisPlugin = createApplicationPlugin("redis-plugin") {
    RedisFactory.init(application.environment.config)
    on(MonitoringEvent(ApplicationStopped)) {
        RedisFactory.close()
    }
}
