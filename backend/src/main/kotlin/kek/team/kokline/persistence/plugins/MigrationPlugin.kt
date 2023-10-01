package kek.team.kokline.persistence.plugins

import io.ktor.server.application.ApplicationStarted
import io.ktor.server.application.createApplicationPlugin
import io.ktor.server.application.hooks.MonitoringEvent
import io.ktor.server.application.log
import kek.team.kokline.persistence.entities.ChatTable
import kek.team.kokline.persistence.entities.ChatUsersTable
import kek.team.kokline.persistence.entities.IncomingMessageTable
import kek.team.kokline.persistence.entities.MessageTable
import kek.team.kokline.persistence.entities.UserTable
import org.jetbrains.exposed.sql.SchemaUtils.statementsRequiredToActualizeScheme
import org.jetbrains.exposed.sql.transactions.transaction

val migrations = createApplicationPlugin("migration-plugin") {
    on(MonitoringEvent(ApplicationStarted)) {
        try {
            transaction {
                val diffStatements = statementsRequiredToActualizeScheme(
                    tables = arrayOf(UserTable, ChatTable, ChatUsersTable, MessageTable, IncomingMessageTable),
                    withLogs = true
                )
                diffStatements.map { exec(it) }
            }
        } catch (e: Throwable) {
            it.log.error("Error during updating tables", e)
        }
    }
}
