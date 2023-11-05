package kek.team.kokline.persistence.plugins

import io.ktor.server.application.ApplicationStarted
import io.ktor.server.application.ApplicationStopped
import io.ktor.server.application.createApplicationPlugin
import io.ktor.server.application.hooks.MonitoringEvent
import kek.team.kokline.factories.DatabaseFactory

val hikariPoolPlugin = createApplicationPlugin("hikari-pool-plugin") {
    DatabaseFactory.init(application.environment.config)
    on(MonitoringEvent(ApplicationStopped)) {
        DatabaseFactory.close()
        it.environment.monitor.unsubscribe(ApplicationStarted) {}
        it.environment.monitor.unsubscribe(ApplicationStopped) {}
    }
}
