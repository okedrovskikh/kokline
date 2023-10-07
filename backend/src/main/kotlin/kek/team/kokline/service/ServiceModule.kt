package kek.team.kokline.service

import kek.team.kokline.service.chat.ChatService
import kek.team.kokline.service.login.LoginService
import kek.team.kokline.service.message.MessageService
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val serviceModule = module {
    singleOf(::MessageService)
    singleOf(::ChatService)
    singleOf(::LoginService)
}
