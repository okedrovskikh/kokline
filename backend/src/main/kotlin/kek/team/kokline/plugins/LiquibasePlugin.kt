package kek.team.kokline.plugins

import io.ktor.server.application.ApplicationStarted
import io.ktor.server.application.createApplicationPlugin
import io.ktor.server.application.hooks.MonitoringEvent
import io.ktor.server.application.log
import kek.team.kokline.factories.DatabaseFactory
import liquibase.Liquibase
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.DirectoryResourceAccessor
import java.io.File
import java.nio.file.Files
import kotlin.io.path.Path

val liquibasePlugin = createApplicationPlugin("liquibase-plugin") {
    on(MonitoringEvent(ApplicationStarted)) { application ->
        if (!application.environment.config.property("liquibase.enabled").getString().toBooleanStrict()) return@on

        // надо нормально переписать
        try {
            val conn = DatabaseFactory.connection
            JdbcConnection(conn).use { liquibaseConnection ->
                DirectoryResourceAccessor(File(System.getProperty("user.dir"))).use { resourceAccessor ->
                    val changeLogFile = application.environment.config.property("liquibase.change-log").getString()
                    Liquibase(changeLogFile, resourceAccessor, liquibaseConnection).use {
                        it.update()
                    }
                }
            }
        } catch (e: Throwable) {
            application.log.error("Error during liquibase running", e)
        }
    }
}
