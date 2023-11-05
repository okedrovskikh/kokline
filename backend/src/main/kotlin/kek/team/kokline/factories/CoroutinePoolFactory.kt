package kek.team.kokline.factories

import io.ktor.server.config.ApplicationConfig
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors

object CoroutinePoolFactory {
    lateinit var coroutinePool: CoroutineDispatcher
        private set

    fun init(config: ApplicationConfig) {
        coroutinePool = Executors.newFixedThreadPool(config.property("coroutine-pool.size").getString().toInt()).asCoroutineDispatcher()
    }

}

@Deprecated("Use coroutine pool from factory", ReplaceWith("CoroutinePoolFactory.coroutinePool"))
val coroutinePool: CoroutineDispatcher get() = CoroutinePoolFactory.coroutinePool
