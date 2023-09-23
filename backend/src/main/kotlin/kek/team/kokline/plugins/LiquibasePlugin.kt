package kek.team.kokline.plugins

import io.ktor.server.application.ApplicationStarted
import io.ktor.server.application.ApplicationStarting
import io.ktor.server.application.createApplicationPlugin
import io.ktor.server.application.hooks.MonitoringEvent
import kek.team.kokline.factories.DatabaseFactory
import liquibase.Contexts
import liquibase.Liquibase
import liquibase.Scope
import liquibase.command.CommandScope
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor

val liquibasePlugin = createApplicationPlugin("liquibase-plugin") {
    on(MonitoringEvent(ApplicationStarted)) { application ->
        if (!application.environment.config.property("liquibase.enabled").getString().toBooleanStrict()) return@on

        /**
         * Че то не работает
         */
        val conn = DatabaseFactory.connection
        JdbcConnection(conn).use { liquibaseConnection ->
            ClassLoaderResourceAccessor().use { resourceAccessor ->
                val changeLogFile = application.environment.config.property("liquibase.change-log").toString()
                Liquibase(changeLogFile, resourceAccessor, liquibaseConnection).use {
                    it.update()
                }
            }
        }

    }
}
