package kek.team.kokline.plugins

import io.ktor.server.application.ApplicationStarted
import io.ktor.server.application.createApplicationPlugin
import io.ktor.server.application.hooks.MonitoringEvent
import io.ktor.server.application.log
import kek.team.kokline.entities.ChatTable
import kek.team.kokline.entities.ChatUsersTable
import kek.team.kokline.entities.MessageTable
import kek.team.kokline.entities.UserTable
import org.jetbrains.exposed.sql.SchemaUtils.statementsRequiredToActualizeScheme
import org.jetbrains.exposed.sql.transactions.transaction

val migrations = createApplicationPlugin("migration-plugin") {
    on(MonitoringEvent(ApplicationStarted)) {
        try {
            transaction {
                val diffStatements = statementsRequiredToActualizeScheme(
                    tables = arrayOf(UserTable, ChatTable, ChatUsersTable, MessageTable),
                    withLogs = true
                )
                diffStatements.map { exec(it) }
            }
        } catch (e: Throwable) {
            it.log.error("Error during updating tables", e)
        }
    }
}
