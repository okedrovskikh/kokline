package kek.team.kokline.service

import kek.team.kokline.service.chat.ChatService
import kek.team.kokline.service.events.ChatEventService
import kek.team.kokline.service.events.EventsServiceFacade
import kek.team.kokline.service.events.MessageEventService
import kek.team.kokline.service.events.UserEventService
import kek.team.kokline.service.login.LoginService
import kek.team.kokline.service.message.MessageService
import kek.team.kokline.service.security.PreferencesService
import kek.team.kokline.service.security.SecurityService
import kek.team.kokline.service.security.UserPreferenceService
import kek.team.kokline.service.user.UserService
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val serviceModule = module {
    singleOf(::MessageService)
    singleOf(::ChatService)
    singleOf(::LoginService)
    singleOf(::UserService)
    singleOf(::EventsServiceFacade)
    singleOf(::PreferencesService)
    singleOf(::SecurityService)
    singleOf(::UserPreferenceService)
    singleOf(::ChatEventService)
    singleOf(::MessageEventService)
    singleOf(::UserEventService)
}
