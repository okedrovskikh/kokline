package kek.team.kokline.plugins

import io.ktor.server.application.ApplicationStarting
import io.ktor.server.application.createApplicationPlugin
import io.ktor.server.application.hooks.MonitoringEvent
import kek.team.kokline.factories.DatabaseFactory
import liquibase.Liquibase
import liquibase.Scope
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor

val liquibasePlugin = createApplicationPlugin("liquibase-plugin") {
    on(MonitoringEvent(ApplicationStarting)) {
        val config = mapOf<String, Any?>()

        Scope.child(config) {
            val conn = DatabaseFactory.connection
            val jdbcConnection = JdbcConnection(conn)
            val liquibase = Liquibase("classpath://resources/db/changelog/db-changelog-master.yaml", ClassLoaderResourceAccessor(), jdbcConnection)
            liquibase.close()
            jdbcConnection.close()
        }
    }
}
