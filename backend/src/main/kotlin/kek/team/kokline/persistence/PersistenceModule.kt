package kek.team.kokline.persistence

import kek.team.kokline.persistence.repositories.ChatRepository
import kek.team.kokline.persistence.repositories.MessageRepository
import kek.team.kokline.persistence.repositories.PreferencesRepository
import kek.team.kokline.persistence.repositories.UserRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val persistenceModule = module {
    singleOf(::MessageRepository)
    singleOf(::ChatRepository)
    singleOf(::UserRepository)
    singleOf(::PreferencesRepository)
}
