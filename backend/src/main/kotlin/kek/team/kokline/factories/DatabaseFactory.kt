package kek.team.kokline.factories

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.config.ApplicationConfig
import io.ktor.util.logging.KtorSimpleLogger
import java.sql.Connection

object DatabaseFactory {
    private lateinit var hikariPool: HikariDataSource

    fun init(config: ApplicationConfig) {
        if (::hikariPool.isInitialized) error("Hikari pool is initialized")

        val driver = config.property("datasource.driver").getString()
        val url = config.property("datasource.url").getString()
        val user = config.property("datasource.user").getString()
        val password = config.property("datasource.password").getString()

        hikariPool = hikariDataSource(url, driver, user, password)
    }

    fun close() {
        hikariPool.close()
    }

    val connection: Connection get() = hikariPool.connection

    private fun hikariDataSource(
        url: String,
        driver: String,
        user: String,
        password: String
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