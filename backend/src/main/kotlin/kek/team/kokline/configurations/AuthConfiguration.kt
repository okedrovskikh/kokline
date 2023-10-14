package kek.team.kokline.configurations

import io.ktor.server.application.Application
import io.ktor.server.auth.authentication
import io.ktor.server.auth.basic
import kek.team.kokline.security.auth.configureBasicAuth
import kek.team.kokline.security.auth.configureChatApiAuth
import kek.team.kokline.security.auth.configureMessageApiAuth
import kek.team.kokline.security.auth.configureUserApiAuth
import kek.team.kokline.service.login.LoginService
import kek.team.kokline.service.security.SecurityService
import org.koin.ktor.ext.inject

/**
 * depends on KoinConfiguration
 * @see kek.team.kokline.configurations.configureKoin
 */
fun Application.configureAuth() {

    val service: LoginService by inject<LoginService>()
    val securityService: SecurityService by inject<SecurityService>()

    authentication {
        basic("auth-basic") {
            realm = "Access to '/' path"
            validate { credential ->
                val userId = service.login(credential)
                securityService.createSession(userId)
            }
        }
    }
    // TODO подумать как можно обобщить создание сессий внутри конфигураций
    configureBasicAuth()
    configureUserApiAuth()
    configureChatApiAuth()
    configureMessageApiAuth()
}
