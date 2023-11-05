package kek.team.kokline.factories

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.config.ApplicationConfig
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.experimental.withSuspendTransaction

object DatabaseFactory {
    private lateinit var hikariPool: HikariDataSource
    private lateinit var database: Database

    fun init(config: ApplicationConfig) {
        if (::hikariPool.isInitialized) error("Hikari pool is initialized")

        val driver = config.property("datasource.driver").getString()
        val url = config.property("datasource.url").getString()
        val user = config.property("datasource.user").getString()
        val password = config.property("datasource.password").getString()

        hikariPool = hikariDataSource(url, driver, user, password)
        database = Database.connect(hikariPool)
    }

    fun close() {
        hikariPool.close()
    }

    private fun hikariDataSource(
        url: String,
        driver: String,
        user: String,
        password: String,
    ) = HikariDataSource(HikariConfig().apply {
        driverClassName = driver
        jdbcUrl = url
        maximumPoolSize = 3
        isAutoCommit = false
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        username = user
        this.password = password
        validate()
    })
}

private val transactionLevel: Int get() = TransactionManager.manager.defaultIsolationLevel

private val currentTransaction: Transaction get() = TransactionManager.current()

private val currentTransactionOrNew: Transaction get() = TransactionManager.currentOrNew(transactionLevel)

suspend fun <T> dbQuery(block: suspend () -> T): T = newSuspendedTransaction { block() }

suspend fun <T> supportedTransaction(block: suspend () -> T): T = currentTransaction.withSuspendTransaction { block() }

suspend fun <T> newOrSupportedTransaction(block: suspend () -> T): T = currentTransactionOrNew.withSuspendTransaction { block() }
