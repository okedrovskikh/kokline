package kek.team.kokline.plugins

import io.ktor.server.application.ApplicationStarted
import io.ktor.server.application.ApplicationStopped
import io.ktor.server.application.createApplicationPlugin
import io.ktor.server.application.hooks.MonitoringEvent
import io.ktor.server.application.log
import kek.team.kokline.factories.DatabaseFactory

val hikariPoolPlugin = createApplicationPlugin("hikari-pool-plugin") {
    /**
     * Если вынести из on, то приложение упадет
     */
    on(MonitoringEvent(ApplicationStarted)) {
        try {
            DatabaseFactory.init(it.environment.config)
        } catch (e: Throwable) {
            it.log.error("Hikari pool initialization exception", e)
        }
    }

    on(MonitoringEvent(ApplicationStopped)) {
        DatabaseFactory.close()
        it.environment.monitor.unsubscribe(ApplicationStarted) {}
        it.environment.monitor.unsubscribe(ApplicationStopped) {}
    }
}
