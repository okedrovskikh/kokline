package kek.team.kokline.service

import kek.team.kokline.service.chat.ChatService
import kek.team.kokline.service.events.EventsService
import kek.team.kokline.service.login.LoginService
import kek.team.kokline.service.message.MessageService
import kek.team.kokline.service.security.PreferencesService
import kek.team.kokline.service.user.UserService
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val serviceModule = module {
    singleOf(::MessageService)
    singleOf(::ChatService)
    singleOf(::LoginService)
    singleOf(::UserService)
    singleOf(::EventsService)
    singleOf(::PreferencesService)
}
