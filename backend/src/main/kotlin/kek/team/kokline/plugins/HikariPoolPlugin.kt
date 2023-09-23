package kek.team.kokline.plugins

import io.ktor.server.application.ApplicationStarting
import io.ktor.server.application.ApplicationStopping
import io.ktor.server.application.createApplicationPlugin
import io.ktor.server.application.hooks.MonitoringEvent
import io.ktor.util.logging.Logger
import kek.team.kokline.factories.DatabaseFactory

val hikariPoolPlugin = createApplicationPlugin("hikari-pool-plugin") {
    on(MonitoringEvent(ApplicationStarting)) {
       DatabaseFactory.init(it.environment.config)
    }

    on(MonitoringEvent(ApplicationStopping)) {
        DatabaseFactory.close()
    }
}
