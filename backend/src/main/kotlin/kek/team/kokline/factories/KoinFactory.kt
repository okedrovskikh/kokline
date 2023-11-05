package kek.team.kokline.factories

import io.ktor.server.application.Application
import org.koin.core.Koin
import org.koin.ktor.ext.getKoin

object KoinFactory {
    lateinit var koin: Koin
        private set

    fun init(application: Application) {
        koin = application.getKoin()
    }

}
